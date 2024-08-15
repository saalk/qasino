package cloud.qasino.games.action.visitor;

import cloud.qasino.games.action.dto.ActionDto;
import cloud.qasino.games.action.dto.Qasino;
import cloud.qasino.games.database.security.Role;
import cloud.qasino.games.database.security.Visitor;
import cloud.qasino.games.database.security.VisitorRepository;
import cloud.qasino.games.database.security.VisitorServiceOld;
import cloud.qasino.games.database.service.VisitorAndLeaguesService;
import cloud.qasino.games.dto.VisitorDto;
import cloud.qasino.games.exception.MyNPException;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Slf4j
@Component
public class RegisterVisitorAction extends ActionDto<EventOutput.Result> {

    @Resource
    VisitorRepository visitorRepository;

    @Autowired
    private VisitorServiceOld visitorServiceOld;
    @Autowired
    private VisitorAndLeaguesService visitorAndLeaguesService;

    @Override
    public EventOutput.Result perform(Qasino qasino) {

        if (!(StringUtils.isEmpty(qasino.getCreation().getSuppliedAlias()))) {
            int sequence = Math.toIntExact(visitorRepository.countByAlias(qasino.getCreation().getSuppliedAlias()));
            sequence++;
            // todo LOW split alias and number
            if (
                    qasino.getCreation().getSuppliedAlias() == null ||
                            qasino.getCreation().getSuppliedEmail() == null ||
                            qasino.getCreation().getSuppliedPassword() == null ||
                            qasino.getCreation().getSuppliedUsername() == null ||
                            qasino.getCreation().getSuppliedAlias().isEmpty() ||
                            qasino.getCreation().getSuppliedEmail().isEmpty() ||
                            qasino.getCreation().getSuppliedPassword().isEmpty() ||
                            qasino.getCreation().getSuppliedUsername().isEmpty()
            ) {
                throw new MyNPException("SignUpNewVisitorAction", "sign_on [" + qasino.getCreation().getSuppliedPassword() + "]");
            }
            VisitorDto createdVisitor = visitorAndLeaguesService.saveNewUser(new Visitor.Builder()
                    .withAlias(qasino.getCreation().getSuppliedAlias())
                    .withAliasSequence(sequence)
                    .withBalance(0)
                    .withEmail(qasino.getCreation().getSuppliedEmail())
                    .withPassword(qasino.getCreation().getSuppliedPassword())
                    .withRoles(Collections.singleton(new Role("ROLE_USER")))
                    .withSecuredLoan(0)
                    .withUsername(qasino.getCreation().getSuppliedUsername())
                    .build());
            qasino.getParams().setSuppliedVisitorId(createdVisitor.getVisitorId());
            qasino.setVisitor(createdVisitor);
        } else {
            setBadRequestErrorMessage(qasino, "username", String.valueOf(qasino.getCreation().getSuppliedUsername()));
            throw new MyNPException("103 gameEvent", "sign_on [" + qasino.getCreation().getSuppliedAlias() + "]");
//            return EventOutput.Result.FAILURE;
        }
        return EventOutput.Result.SUCCESS;
    }

    private void setBadRequestErrorMessage(Qasino qasino, String id, String value) {
        qasino.getMessage().setErrorKey(id);
        qasino.getMessage().setErrorValue(value);
        qasino.getMessage().setBadRequestErrorMessage("empty");
    }

    private void setConflictErrorMessage(Qasino qasino, String id, String value) {
        qasino.getMessage().setErrorKey(id);
        qasino.getMessage().setErrorValue(value);
        qasino.getMessage().setConflictErrorMessage("username [" + value + "] not available any more");
    }

}
