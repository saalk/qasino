package cloud.qasino.games.action;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.action.util.ActionUtils;
import cloud.qasino.games.database.security.Visitor;
import cloud.qasino.games.database.security.VisitorRepository;
import cloud.qasino.games.dto.QasinoFlowDTO;
import cloud.qasino.games.exception.MyNPException;
import cloud.qasino.games.statemachine.event.EventOutput;
import cloud.qasino.games.statemachine.event.GameEvent;
import cloud.qasino.games.statemachine.event.TurnEvent;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class FindVisitorIdByAliasOrUsernameAction implements Action<FindVisitorIdByAliasOrUsernameAction.Dto, EventOutput.Result> {

    @Resource
    VisitorRepository visitorRepository;

    @Override
    public EventOutput.Result perform(Dto actionDto) {

        if (actionDto.getSuppliedVisitorId() > 0) {
            return EventOutput.Result.SUCCESS;
        }

        Optional<Visitor> foundVisitor;
        // set http to created when done
        if (!(StringUtils.isEmpty(actionDto.getSuppliedAlias()))) {
            int sequence = Math.toIntExact(visitorRepository.countByAlias(actionDto.getSuppliedAlias()));
            if (sequence > 1) {
                // todo LOW split alias and number
                setConflictErrorMessage(actionDto, "alias", String.valueOf(actionDto.getSuppliedAlias()));
                return EventOutput.Result.FAILURE;
            } else if (sequence == 0) {
                setNotFoundErrorMessage(actionDto, "alias", String.valueOf(actionDto.getSuppliedAlias()));
                return EventOutput.Result.FAILURE;
            }
            foundVisitor = visitorRepository.findVisitorByAliasAndAliasSequence(actionDto.getSuppliedAlias(), 1);
        } else if (!(StringUtils.isEmpty(actionDto.getSuppliedUsername()))) {
            foundVisitor = Optional.ofNullable(visitorRepository.findByUsername(actionDto.getSuppliedUsername()));
        } else {
            return EventOutput.Result.SUCCESS;
        }

        if (foundVisitor.isPresent()) {
            actionDto.setSuppliedVisitorId(foundVisitor.get().getVisitorId());
        } else {
            throw new MyNPException("55 FindVisitorIdByAliasOrUsernameAction","actionDto.getSuppliedUsername() [" + actionDto.getSuppliedUsername() + "]");

//            setNotFoundErrorMessage(actionDto, "visitor", String.valueOf(actionDto.getSuppliedAlias()));
//            return EventOutput.Result.FAILURE;
        }
        return EventOutput.Result.SUCCESS;
    }

    private void setNotFoundErrorMessage(Dto actionDto, String id, String value) {
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setNotFoundErrorMessage("");
    }

    private void setBadRequestErrorMessage(Dto actionDto, String id, String value) {
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setBadRequestErrorMessage("");
    }

    private void setConflictErrorMessage(Dto actionDto, String id, String value) {
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setConflictErrorMessage("Multiple " + id + " found for supplied value [" + value +
                "]");
    }

    public interface Dto {

        // @formatter:off
        String getErrorMessage();
        GameEvent getSuppliedGameEvent();
        TurnEvent getSuppliedTurnEvent();

        // Getters
        long getSuppliedVisitorId();
        String getSuppliedAlias();
        String getSuppliedUsername();

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