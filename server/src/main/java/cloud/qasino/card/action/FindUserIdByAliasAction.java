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
public class FindUserIdByAliasAction implements Action<FindUserIdByAliasAction.FindUserIdByAliasActionDTO, EventOutput.Result> {

    @Resource
    UserRepository userRepository;

    @Override
    public EventOutput.Result perform(FindUserIdByAliasActionDTO actionDto) {

        log.debug("Action: FindUserByAliasAction");

        // set http to created when done

        if (!(StringUtils.isEmpty(actionDto.getSuppliedAlias()))) {
            int sequence = Math.toIntExact(userRepository.countByAlias(actionDto.getSuppliedAlias()));
            if (sequence > 1) {
                // todo LOW split alias and number
                setErrorMessageConflict(actionDto, "alias", String.valueOf(actionDto.getSuppliedAlias()));
                return EventOutput.Result.FAILURE;
            } else if (sequence == 0) {
                setErrorMessageNotFound(actionDto, "alias", String.valueOf(actionDto.getSuppliedAlias()));
                return EventOutput.Result.FAILURE;
            }
            Optional<User> foundUser = userRepository.findUserByAliasAndAliasSequence(actionDto.getSuppliedAlias(), 1);
            if (foundUser.isPresent()) {
                actionDto.setSuppliedUserId(foundUser.get().getUserId());
                // call FindAllEntitiesForInputAction after this to do the actual retrieval
            } else {
                setErrorMessageNotFound(actionDto, "userId", actionDto.getSuppliedAlias());
                return EventOutput.Result.FAILURE;
            }
        } else {
            setErrorMessageBadRequest(actionDto, "alias", String.valueOf(actionDto.getSuppliedAlias()));
            return EventOutput.Result.FAILURE;
        }
        return EventOutput.Result.SUCCESS;
    }

    private void setErrorMessageNotFound(FindUserIdByAliasActionDTO actionDto, String id,
                                         String value) {
        actionDto.setHttpStatus(404);
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setErrorMessage(id + " not found for supplied value [" + value + "]");
        actionDto.setUriAndHeaders();
    }

    private void setErrorMessageBadRequest(FindUserIdByAliasActionDTO actionDto, String id,
                                           String value) {
        actionDto.setHttpStatus(400);
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setErrorMessage("Supplied value " + id + " is empty");
        actionDto.setUriAndHeaders();
    }


    private void setErrorMessageConflict(FindUserIdByAliasActionDTO actionDto, String id,
                                         String value) {
        actionDto.setHttpStatus(409);
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setErrorMessage("Multiple " + id + " found for supplied value [" + value +
                "]");
        actionDto.setUriAndHeaders();
    }

    public interface FindUserIdByAliasActionDTO {

        // @formatter:off
        // Getters
        String getSuppliedAlias();

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