package cloud.qasino.games.action;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.entity.User;
import cloud.qasino.games.event.EventOutput;
import cloud.qasino.games.database.repository.UserRepository;
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

        if (!(StringUtils.isEmpty(actionDto.getSuppliedUserName()))) {
            int sequence = Math.toIntExact(userRepository.countByUserName(actionDto.getSuppliedUserName()));
            if (sequence != 0) {
                setErrorMessageConflict(actionDto, "userName", String.valueOf(actionDto.getSuppliedUserName()));
                return EventOutput.Result.FAILURE;
            }
            // todo LOW split userName and number
            User createdUser = userRepository.save(new User(actionDto.getSuppliedUserName(), 1,
                    actionDto.getSuppliedEmail()));
            if (createdUser.getUserId() == 0) {
                setErrorMessageInternalServerError(actionDto, "userName", String.valueOf(actionDto.getSuppliedUserName()));
                return EventOutput.Result.FAILURE;
            }
            actionDto.setSuppliedUserId(createdUser.getUserId());
            // call FindAllEntitiesForInputAction after this to do the actual retrieval
        } else {
            setErrorMessageBadRequest(actionDto, "userName", String.valueOf(actionDto.getSuppliedUserName()));
            return EventOutput.Result.FAILURE;
        }
        return EventOutput.Result.SUCCESS;
    }

    private void setErrorMessageBadRequest(SignUpNewUserActionDTO actionDto, String id,
                                           String value) {
        actionDto.setHttpStatus(400);
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setErrorMessage("Supplied value for userName is empty");
        actionDto.setUriAndHeaders();
    }


    private void setErrorMessageConflict(SignUpNewUserActionDTO actionDto, String id,
                                         String value) {
        actionDto.setHttpStatus(409);
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setErrorMessage("UserName [" + value + "] not available any more");
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
        String getSuppliedUserName();
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
