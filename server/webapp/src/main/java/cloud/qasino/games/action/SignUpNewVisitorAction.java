package cloud.qasino.games.action;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.security.Role;
import cloud.qasino.games.database.security.Visitor;
import cloud.qasino.games.database.security.VisitorServiceOld;
import cloud.qasino.games.exception.MyNPException;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import cloud.qasino.games.database.security.VisitorRepository;
import cloud.qasino.games.pattern.statemachine.event.GameEvent;
import cloud.qasino.games.pattern.statemachine.event.TurnEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

import java.util.Collections;

@Slf4j
@Component
public class SignUpNewVisitorAction implements Action<SignUpNewVisitorAction.Dto, EventOutput.Result> {

    @Resource
    VisitorRepository visitorRepository;

    @Autowired
    private VisitorServiceOld visitorServiceOld;


    @Override
    public EventOutput.Result perform(Dto actionDto) {

        if (!(StringUtils.isEmpty(actionDto.getSuppliedAlias()))) {
            int sequence = Math.toIntExact(visitorRepository.countByAlias(actionDto.getSuppliedAlias()));
            sequence++;
            // todo LOW split alias and number
            if (
                    actionDto.getSuppliedAlias()==null ||
                    actionDto.getSuppliedEmail()==null ||
                    actionDto.getSuppliedPassword()==null ||
                    actionDto.getSuppliedUsername()==null ||
                    actionDto.getSuppliedAlias().isEmpty() ||
                    actionDto.getSuppliedEmail().isEmpty() ||
                    actionDto.getSuppliedPassword().isEmpty() ||
                    actionDto.getSuppliedUsername().isEmpty()
            ){
                throw new MyNPException("SignUpNewVisitorAction","sign_on [" + actionDto.getSuppliedPassword() + "]");
            }
//            Visitor visitor = Visitor.buildDummy(actionDto.getSuppliedUsername(), actionDto.getSuppliedAlias());
            Visitor createdVisitor = visitorServiceOld.saveUser(new Visitor.Builder()
                    .withAlias(actionDto.getSuppliedAlias())
                    .withAliasSequence(sequence)
                    .withBalance(0)
                    .withEmail(actionDto.getSuppliedEmail())
                    .withPassword(actionDto.getSuppliedPassword())
                    .withRoles(Collections.singleton(new Role("ROLE_USER")))
                    .withSecuredLoan(0)
                    .withUsername(actionDto.getSuppliedUsername())
                    .build());
            actionDto.setSuppliedVisitorId(createdVisitor.getVisitorId());
        } else {
            setBadRequestErrorMessage(actionDto, "username", String.valueOf(actionDto.getSuppliedUsername()));
            throw new MyNPException("103 gameEvent","sign_on [" + actionDto.getSuppliedAlias() + "]");
//            return EventOutput.Result.FAILURE;
        }
        return EventOutput.Result.SUCCESS;
    }

     private void setBadRequestErrorMessage(Dto actionDto, String id, String value) {
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setBadRequestErrorMessage("empty");
    }

    private void setConflictErrorMessage(Dto actionDto, String id, String value) {
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setConflictErrorMessage("username [" + value + "] not available any more");
    }

    public interface Dto {

        // @formatter:off
        String getErrorMessage();
        GameEvent getSuppliedGameEvent();
        TurnEvent getSuppliedTurnEvent();

        // Getters
        String getSuppliedUsername();
        String getSuppliedPassword();
        String getSuppliedEmail();
        String getSuppliedAlias();

        // Setter
        void setSuppliedVisitorId(long id);

        // error setters
        // @formatter:off
        void setBadRequestErrorMessage(String problem);
        void setNotFoundErrorMessage(String problem);
        void setConflictErrorMessage(String reason);
        void setUnprocessableErrorMessage(String reason);
        void setErrorKey(String errorKey);
        void setErrorValue(String errorValue);
        // @formatter:on
    }
}
