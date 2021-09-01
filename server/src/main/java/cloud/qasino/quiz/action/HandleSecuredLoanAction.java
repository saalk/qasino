package cloud.qasino.quiz.action;

import cloud.qasino.quiz.action.interfaces.Action;
import cloud.qasino.quiz.entity.User;
import cloud.qasino.quiz.event.EventOutput;
import cloud.qasino.quiz.repository.UserRepository;
import cloud.qasino.quiz.util.Systemout;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Random;

@Slf4j
@Component
public class HandleSecuredLoanAction implements Action<HandleSecuredLoanAction.HandleSecuredLoanActionDTO, EventOutput.Result> {

    @Resource
    UserRepository userRepository;

    @Override
    public EventOutput.Result perform(HandleSecuredLoanActionDTO actionDto) {

        log.debug("Action: HandleSecuredLoanAction");

        EventOutput.Result result = EventOutput.Result.FAILURE;
        User updateUser = actionDto.getGameUser();

        if (actionDto.isRequestingToRepay()) {
            Systemout.handleSecuredLoanActionFlow(actionDto); //todo delete
            if (!updateUser.repayLoan()) {
                Systemout.handleSecuredLoanActionFlow(actionDto); //todo delete

                setErrorMessagConflict(actionDto, "isRequestingToRepay", "true");
                return EventOutput.Result.FAILURE;
            }
            actionDto.setGameUser(userRepository.save(updateUser));
            Systemout.handleSecuredLoanActionFlow(actionDto); //todo delete

            result = EventOutput.Result.SUCCESS;
        }

        if (actionDto.isOfferingShipForPawn()) {
            Random random = new Random();
            int randomNumber = random.nextInt(1001);
            Systemout.handleSecuredLoanActionFlow(actionDto); //todo delete
            if (!updateUser.pawnShip(randomNumber)) {
                Systemout.handleSecuredLoanActionFlow(actionDto); //todo delete
                setErrorMessagConflict(actionDto,"isOfferingShipForPawn", "true");
                return EventOutput.Result.FAILURE;
            }
            actionDto.setGameUser(userRepository.save(updateUser));
            Systemout.handleSecuredLoanActionFlow(actionDto); //todo delete

            result = EventOutput.Result.SUCCESS;
        }
        if (result == EventOutput.Result.FAILURE) {
            setErrorMessageBadRequest(actionDto, "<HandleSecuredLoanAction>", "true");
        }
        return result;
    }

    private void setErrorMessagConflict(HandleSecuredLoanActionDTO actionDto, String id,
                                        String value) {
        actionDto.setHttpStatus(409);
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setErrorMessage("Action " + id + " not valid");
        actionDto.setUriAndHeaders();
    }
    private void setErrorMessageBadRequest(HandleSecuredLoanActionDTO actionDto, String id,
                                           String value) {
        actionDto.setHttpStatus(400);
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setErrorMessage("Supplied value " + id + " is not available");
        actionDto.setUriAndHeaders();
    }
    public interface HandleSecuredLoanActionDTO {

        // @formatter:off
        // Getters

        boolean isRequestingToRepay();
        boolean isOfferingShipForPawn();
        User getGameUser();

        // Setters
        void setGameUser(User user);

        // error setters
        void setHttpStatus(int status);
        void setErrorKey(String key);
        void setErrorValue(String value);
        void setErrorMessage(String key);
        void setUriAndHeaders();
        // @formatter:on
    }
}
