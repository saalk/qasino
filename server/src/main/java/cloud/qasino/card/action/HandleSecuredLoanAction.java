package cloud.qasino.card.action;

import cloud.qasino.card.action.interfaces.Action;
import cloud.qasino.card.entity.User;
import cloud.qasino.card.event.EventOutput;
import cloud.qasino.card.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
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

        if (actionDto.isRequestingToRepay()) {
            if (!actionDto.getGameUser().repayLoan()) {
                setErrorMessagConflict(actionDto, String.valueOf(actionDto.isRequestingToRepay()), "true");
                return EventOutput.Result.FAILURE;
            }
            actionDto.setGameUser(userRepository.save(actionDto.getGameUser()));

        }
        if (actionDto.isOfferingShipForPawn()) {
            Random random = new Random();
            int randomNumber = random.nextInt(1001);
            if (!actionDto.getGameUser().pawnShip(randomNumber)) {
                setErrorMessagConflict(actionDto, String.valueOf(actionDto.isRequestingToRepay()), "true");
                return EventOutput.Result.FAILURE;
            }
            actionDto.setGameUser(userRepository.save(actionDto.getGameUser()));
        }
        return EventOutput.Result.SUCCESS;
    }

    private void setErrorMessagConflict(HandleSecuredLoanActionDTO actionDto, String id,
                                        String value) {
        actionDto.setHttpStatus(409);
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setErrorMessage("Action " + id + " not valid");
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
