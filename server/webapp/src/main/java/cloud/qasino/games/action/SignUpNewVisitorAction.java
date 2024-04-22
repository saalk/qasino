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
                setErrorMessageConflict(actionDto, "visitorName", String.valueOf(actionDto.getSuppliedVisitorName()));
                return EventOutput.Result.FAILURE;
            }
            // todo LOW split visitorName and number
            Visitor createdVisitor = visitorRepository.save(new Visitor(actionDto.getSuppliedVisitorName(), 1,
                    actionDto.getSuppliedEmail()));
            if (createdVisitor.getVisitorId() == 0) {
                setErrorMessageInternalServerError(actionDto, "visitorName", String.valueOf(actionDto.getSuppliedVisitorName()));
                return EventOutput.Result.FAILURE;
            }
            actionDto.setSuppliedVisitorId(createdVisitor.getVisitorId());
        } else {
            setErrorMessageBadRequest(actionDto, "visitorName", String.valueOf(actionDto.getSuppliedVisitorName()));
            return EventOutput.Result.FAILURE;
        }
        return EventOutput.Result.SUCCESS;
    }

    private void setErrorMessageBadRequest(SignUpNewVisitorActionDTO actionDto, String id,
                                           String value) {
        actionDto.setHttpStatus(400);
        actionDto.setKey(id);
        actionDto.setValue(value);
        actionDto.setErrorMessage("Supplied value for visitorName is empty");
    }


    private void setErrorMessageConflict(SignUpNewVisitorActionDTO actionDto, String id,
                                         String value) {
        actionDto.setHttpStatus(409);
        actionDto.setKey(id);
        actionDto.setValue(value);
        actionDto.setErrorMessage("visitorName [" + value + "] not available any more");
    }

    private void setErrorMessageInternalServerError(SignUpNewVisitorActionDTO actionDto, String id,
                                                    String value) {
        actionDto.setHttpStatus(500);
        actionDto.setKey(id);
        actionDto.setValue(value);
        actionDto.setErrorMessage("Crash while signing up a new visitor");
    }

    public interface SignUpNewVisitorActionDTO {

        // @formatter:off
        // Getters
        String getSuppliedVisitorName();
        String getSuppliedEmail();

        // Setter
        void setSuppliedVisitorId(long id);

        // error setters
        void setHttpStatus(int status);
        void setKey(String key);
        void setValue(String value);
        void setErrorMessage(String key);
        // @formatter:on
    }
}
