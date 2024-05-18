package cloud.qasino.games.action;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.security.Visitor;
import cloud.qasino.games.statemachine.event.EventOutput;
import cloud.qasino.games.database.security.VisitorRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class SignUpNewVisitorAction implements Action<SignUpNewVisitorAction.SignUpNewVisitorActionDTO, EventOutput.Result> {

    @Resource
    VisitorRepository visitorRepository;

    @Override
    public EventOutput.Result perform(SignUpNewVisitorActionDTO actionDto) {

        if (!(StringUtils.isEmpty(actionDto.getSuppliedAlias()))) {
            int sequence = Math.toIntExact(visitorRepository.countByAlias(actionDto.getSuppliedAlias()));
            if (sequence != 0) {
                setConflictErrorMessage(actionDto, "alias", String.valueOf(actionDto.getSuppliedAlias()));
                return EventOutput.Result.FAILURE;
            }
            // todo LOW split alias and number
            Visitor createdVisitor = visitorRepository.save(
                    new Visitor.Builder()
                            .withUsername(actionDto.getSuppliedUsername())
                            .withPassword(actionDto.getSuppliedPassword())
                            .withEmail(actionDto.getSuppliedEmail())
                            .withAlias(actionDto.getSuppliedAlias())
                            .withAliasSequence(1)
                            .build());
            actionDto.setSuppliedVisitorId(createdVisitor.getVisitorId());
        } else {
            setBadRequestErrorMessage(actionDto, "username", String.valueOf(actionDto.getSuppliedUsername()));
            return EventOutput.Result.FAILURE;
        }
        return EventOutput.Result.SUCCESS;
    }

     private void setBadRequestErrorMessage(SignUpNewVisitorActionDTO actionDto, String id, String value) {
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setBadRequestErrorMessage("empty");
    }

    private void setConflictErrorMessage(SignUpNewVisitorActionDTO actionDto, String id, String value) {
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setConflictErrorMessage("username [" + value + "] not available any more");
    }

    public interface SignUpNewVisitorActionDTO {

        // @formatter:off
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
