package cloud.qasino.games.action;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.entity.Visitor;
import cloud.qasino.games.event.EventOutput;
import cloud.qasino.games.database.repository.VisitorRepository;
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

        if (!(StringUtils.isEmpty(actionDto.getSuppliedVisitorName()))) {
            int sequence = Math.toIntExact(visitorRepository.countByVisitorName(actionDto.getSuppliedVisitorName()));
            if (sequence != 0) {
                setConflictErrorMessage(actionDto, "visitorName", String.valueOf(actionDto.getSuppliedVisitorName()));
                return EventOutput.Result.FAILURE;
            }
            // todo LOW split visitorName and number
            Visitor createdVisitor = visitorRepository.save(new Visitor(actionDto.getSuppliedVisitorName(), 1,
                    actionDto.getSuppliedEmail()));
            actionDto.setSuppliedVisitorId(createdVisitor.getVisitorId());
        } else {
            setBadRequestErrorMessage(actionDto, "visitorName", String.valueOf(actionDto.getSuppliedVisitorName()));
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
        actionDto.setConflictErrorMessage("visitorName [" + value + "] not available any more");
    }

    public interface SignUpNewVisitorActionDTO {

        // @formatter:off
        // Getters
        String getSuppliedVisitorName();
        String getSuppliedEmail();

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
