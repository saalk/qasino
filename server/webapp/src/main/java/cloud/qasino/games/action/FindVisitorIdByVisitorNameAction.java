package cloud.qasino.games.action;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.entity.Visitor;
import cloud.qasino.games.database.repository.VisitorRepository;
import cloud.qasino.games.event.EventOutput;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Optional;

@Slf4j
@Component
public class FindVisitorIdByVisitorNameAction implements Action<FindVisitorIdByVisitorNameAction.FindVisitorIdByVisitorNameActionDTO, EventOutput.Result> {

    @Resource
    VisitorRepository visitorRepository;

    @Override
    public EventOutput.Result perform(FindVisitorIdByVisitorNameActionDTO actionDto) {

        log.debug("Action: FindVisitorByVisitorNameAction");

        // set http to created when done

        if (!(StringUtils.isEmpty(actionDto.getSuppliedVisitorName()))) {
            int sequence = Math.toIntExact(visitorRepository.countByVisitorName(actionDto.getSuppliedVisitorName()));
            if (sequence > 1) {
                // todo LOW split visitorName and number
                setErrorMessageConflict(actionDto, "visitorName", String.valueOf(actionDto.getSuppliedVisitorName()));
                return EventOutput.Result.FAILURE;
            } else if (sequence == 0) {
                setErrorMessageNotFound(actionDto, "visitorName", String.valueOf(actionDto.getSuppliedVisitorName()));
                return EventOutput.Result.FAILURE;
            }
            Optional<Visitor> foundVisitor = visitorRepository.findVisitorByVisitorNameAndVisitorNameSequence(actionDto.getSuppliedVisitorName(), 1);
            if (foundVisitor.isPresent()) {
                actionDto.setSuppliedVisitorId(foundVisitor.get().getVisitorId());
                // call FindAllEntitiesForInputAction after this to do the actual retrieval
            } else {
                setErrorMessageNotFound(actionDto, "visitorId", actionDto.getSuppliedVisitorName());
                return EventOutput.Result.FAILURE;
            }
        } else {
            setErrorMessageBadRequest(actionDto, "visitorName", String.valueOf(actionDto.getSuppliedVisitorName()));
            return EventOutput.Result.FAILURE;
        }
        return EventOutput.Result.SUCCESS;
    }

    private void setErrorMessageNotFound(FindVisitorIdByVisitorNameActionDTO actionDto, String id,
                                         String value) {
        actionDto.setHttpStatus(404);
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setErrorMessage(id + " not found for supplied value [" + value + "]");
        actionDto.prepareResponseHeaders();
    }

    private void setErrorMessageBadRequest(FindVisitorIdByVisitorNameActionDTO actionDto, String id,
                                           String value) {
        actionDto.setHttpStatus(400);
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setErrorMessage("Supplied value " + id + " is empty");
        actionDto.prepareResponseHeaders();
    }


    private void setErrorMessageConflict(FindVisitorIdByVisitorNameActionDTO actionDto, String id,
                                         String value) {
        actionDto.setHttpStatus(409);
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setErrorMessage("Multiple " + id + " found for supplied value [" + value +
                "]");
        actionDto.prepareResponseHeaders();
    }

    public interface FindVisitorIdByVisitorNameActionDTO {

        // @formatter:off
        // Getters
        String getSuppliedVisitorName();

        // Setter
        void setSuppliedVisitorId(long id);

        // error setters
        void setHttpStatus(int status);
        void setErrorKey(String key);
        void setErrorValue(String value);
        void setErrorMessage(String key);
        void prepareResponseHeaders();
        // @formatter:on
    }
}