package cloud.qasino.card.action;

import cloud.qasino.card.action.interfaces.Action;
import cloud.qasino.card.entity.User;
import cloud.qasino.card.event.EventOutput;
import cloud.qasino.card.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class SignUpNewUserAction implements Action<SignUpNewUserAction.SignUpNewUserActionDTO, EventOutput.Result> {

    @Resource
    UserRepository userRepository;

    @Override
    public EventOutput.Result perform(SignUpNewUserActionDTO actionDto) {

        log.debug("Action: SignUpNewUserAction");

        if (!(StringUtils.isEmpty(actionDto.getSuppliedAlias()))) {
            int sequence = Math.toIntExact(userRepository.countByAlias(actionDto.getSuppliedAlias()));
            if (sequence != 0) {
                setErrorMessageConflict(actionDto, "alias", String.valueOf(actionDto.getSuppliedAlias()));
                return EventOutput.Result.FAILURE;
            }
            // todo LOW split alias and number
            User createdUser = userRepository.save(new User(actionDto.getSuppliedAlias(), 1,
                    actionDto.getSuppliedEmail()));
            if (createdUser.getUserId() == 0) {
                setErrorMessageInternalServerError(actionDto, "alias", String.valueOf(actionDto.getSuppliedAlias()));
                return EventOutput.Result.FAILURE;
            }
            actionDto.setSuppliedUserId(createdUser.getUserId());
            // call FindAllEntitiesForInputAction after this to do the actual retrieval
        } else {
            setErrorMessageBadRequest(actionDto, "alias", String.valueOf(actionDto.getSuppliedAlias()));
            return EventOutput.Result.FAILURE;
        }
        return EventOutput.Result.SUCCESS;
    }

    private void setErrorMessageBadRequest(SignUpNewUserActionDTO actionDto, String id,
                                           String value) {
        actionDto.setHttpStatus(400);
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setErrorMessage("Supplied value for alias is empty");
        actionDto.setUriAndHeaders();
    }


    private void setErrorMessageConflict(SignUpNewUserActionDTO actionDto, String id,
                                         String value) {
        actionDto.setHttpStatus(409);
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setErrorMessage("Alias [" + value + "] not available any more");
        actionDto.setUriAndHeaders();
    }

    private void setErrorMessageInternalServerError(SignUpNewUserActionDTO actionDto, String id,
                                                    String value) {
        actionDto.setHttpStatus(500);
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setErrorMessage("Crash while signing up a new user");
        actionDto.setUriAndHeaders();
    }

    public interface SignUpNewUserActionDTO {

        // @formatter:off
        // Getters
        String getSuppliedAlias();
        String getSuppliedEmail();

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
