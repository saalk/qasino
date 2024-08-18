package cloud.qasino.games.action.visitor;

import cloud.qasino.games.action.common.GenericLookupsAction;
import cloud.qasino.games.database.security.Role;
import cloud.qasino.games.database.security.Visitor;
import cloud.qasino.games.database.service.VisitorService;
import cloud.qasino.games.dto.Qasino;
import cloud.qasino.games.dto.model.VisitorDto;
import cloud.qasino.games.exception.MyNPException;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Slf4j
@Component
public class RegisterVisitorAction extends GenericLookupsAction<EventOutput.Result> {

    // @formatter:off
    @Autowired private VisitorService visitorService;
    // @formatter:on

    @Override
    public EventOutput.Result perform(Qasino qasino) {

        if (!(StringUtils.isEmpty(qasino.getCreation().getSuppliedAlias()))) {
            int sequence = Math.toIntExact(visitorService.countByAlias(qasino.getCreation().getSuppliedAlias()));
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
            VisitorDto createdVisitor = visitorService.saveNewUser(new Visitor.Builder()
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
            qasino.getMessage().setBadRequestErrorMessage("alias", String.valueOf(qasino.getCreation().getSuppliedAlias()),"empty");
            return EventOutput.Result.FAILURE;
        }
        return EventOutput.Result.SUCCESS;
    }
}
