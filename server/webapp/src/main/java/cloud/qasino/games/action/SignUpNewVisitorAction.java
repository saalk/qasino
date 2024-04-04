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

        log.debug("Action: SignUpNewVisitorAction");

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
            // call FindAllEntitiesForInputAction after this to do the actual retrieval
        } else {
            setErrorMessageBadRequest(actionDto, "visitorName", String.valueOf(actionDto.getSuppliedVisitorName()));
            return EventOutput.Result.FAILURE;
        }
        return EventOutput.Result.SUCCESS;
    }

    private void setErrorMessageBadRequest(SignUpNewVisitorActionDTO actionDto, String id,
                                           String value) {
        actionDto.setHttpStatus(400);
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setErrorMessage("Supplied value for visitorName is empty");
        actionDto.setUriAndHeaders();
    }


    private void setErrorMessageConflict(SignUpNewVisitorActionDTO actionDto, String id,
                                         String value) {
        actionDto.setHttpStatus(409);
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setErrorMessage("VisitorName [" + value + "] not available any more");
        actionDto.setUriAndHeaders();
    }

    private void setErrorMessageInternalServerError(SignUpNewVisitorActionDTO actionDto, String id,
                                                    String value) {
        actionDto.setHttpStatus(500);
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setErrorMessage("Crash while signing up a new visitor");
        actionDto.setUriAndHeaders();
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
        void setErrorKey(String key);
        void setErrorValue(String value);
        void setErrorMessage(String key);
        void setUriAndHeaders();
        // @formatter:on
    }
}
