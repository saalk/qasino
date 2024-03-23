package cloud.qasino.card.action;

import cloud.qasino.card.action.interfaces.Action;
import cloud.qasino.card.entity.User;
import cloud.qasino.card.event.EventOutput;
import cloud.qasino.card.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Optional;

@Slf4j
@Component
public class FindUserIdByUserNameAction implements Action<FindUserIdByUserNameAction.FindUserIdByUserNameActionDTO, EventOutput.Result> {

    @Resource
    UserRepository userRepository;

    @Override
    public EventOutput.Result perform(FindUserIdByUserNameActionDTO actionDto) {

        log.debug("Action: FindUserByUserNameAction");

        // set http to created when done

        if (!(StringUtils.isEmpty(actionDto.getSuppliedUserName()))) {
            int sequence = Math.toIntExact(userRepository.countByUserName(actionDto.getSuppliedUserName()));
            if (sequence > 1) {
                // todo LOW split userName and number
                setErrorMessageConflict(actionDto, "userName", String.valueOf(actionDto.getSuppliedUserName()));
                return EventOutput.Result.FAILURE;
            } else if (sequence == 0) {
                setErrorMessageNotFound(actionDto, "userName", String.valueOf(actionDto.getSuppliedUserName()));
                return EventOutput.Result.FAILURE;
            }
            Optional<User> foundUser = userRepository.findUserByUserNameAndUserNameSequence(actionDto.getSuppliedUserName(), 1);
            if (foundUser.isPresent()) {
                actionDto.setSuppliedUserId(foundUser.get().getUserId());
                // call FindAllEntitiesForInputAction after this to do the actual retrieval
            } else {
                setErrorMessageNotFound(actionDto, "userId", actionDto.getSuppliedUserName());
                return EventOutput.Result.FAILURE;
            }
        } else {
            setErrorMessageBadRequest(actionDto, "userName", String.valueOf(actionDto.getSuppliedUserName()));
            return EventOutput.Result.FAILURE;
        }
        return EventOutput.Result.SUCCESS;
    }

    private void setErrorMessageNotFound(FindUserIdByUserNameActionDTO actionDto, String id,
                                         String value) {
        actionDto.setHttpStatus(404);
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setErrorMessage(id + " not found for supplied value [" + value + "]");
        actionDto.setUriAndHeaders();
    }

    private void setErrorMessageBadRequest(FindUserIdByUserNameActionDTO actionDto, String id,
                                           String value) {
        actionDto.setHttpStatus(400);
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setErrorMessage("Supplied value " + id + " is empty");
        actionDto.setUriAndHeaders();
    }


    private void setErrorMessageConflict(FindUserIdByUserNameActionDTO actionDto, String id,
                                         String value) {
        actionDto.setHttpStatus(409);
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setErrorMessage("Multiple " + id + " found for supplied value [" + value +
                "]");
        actionDto.setUriAndHeaders();
    }

    public interface FindUserIdByUserNameActionDTO {

        // @formatter:off
        // Getters
        String getSuppliedUserName();

        // Setter
        void setSuppliedUserId(int id);

        // error setters
        void setHttpStatus(int status);
        void setErrorKey(String key);
        void setErrorValue(String value);
        void setErrorMessage(String key);
        void setUriAndHeaders();
        // @formatter:on
    }
}