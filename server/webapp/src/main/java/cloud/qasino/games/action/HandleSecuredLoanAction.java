package cloud.qasino.games.action;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.entity.Visitor;
import cloud.qasino.games.event.EventOutput;
import cloud.qasino.games.database.repository.VisitorRepository;
import cloud.qasino.games.util.Systemout;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Random;

@Slf4j
@Component
public class HandleSecuredLoanAction implements Action<HandleSecuredLoanAction.HandleSecuredLoanActionDTO, EventOutput.Result> {

    @Resource
    VisitorRepository visitorRepository;

    @Override
    public EventOutput.Result perform(HandleSecuredLoanActionDTO actionDto) {

        log.debug("Action: HandleSecuredLoanAction");

        EventOutput.Result result = EventOutput.Result.FAILURE;
        Visitor updateVisitor = actionDto.getQasinoVisitor();

        if (actionDto.isRequestingToRepay()) {
//            Systemout.handleSecuredLoanActionFlow(actionDto); //todo delete
            if (!updateVisitor.repayLoan()) {
//                Systemout.handleSecuredLoanActionFlow(actionDto); //todo delete

                setErrorMessagConflict(actionDto, "isRequestingToRepay", "true");
                return EventOutput.Result.FAILURE;
            }
            actionDto.setQasinoVisitor(visitorRepository.save(updateVisitor));
//            Systemout.handleSecuredLoanActionFlow(actionDto); //todo delete

            result = EventOutput.Result.SUCCESS;
        }

        if (actionDto.isOfferingShipForPawn()) {
            Random random = new Random();
            int randomNumber = random.nextInt(1001);
//            Systemout.handleSecuredLoanActionFlow(actionDto); //todo delete
            if (!updateVisitor.pawnShip(randomNumber)) {
//                Systemout.handleSecuredLoanActionFlow(actionDto); //todo delete
                setErrorMessagConflict(actionDto,"isOfferingShipForPawn", "true");
                return EventOutput.Result.FAILURE;
            }
            actionDto.setQasinoVisitor(visitorRepository.save(updateVisitor));
//            Systemout.handleSecuredLoanActionFlow(actionDto); //todo delete

            result = EventOutput.Result.SUCCESS;
        }
        // todo should be 500
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
        actionDto.prepareResponseHeaders();
    }
    private void setErrorMessageBadRequest(HandleSecuredLoanActionDTO actionDto, String id,
                                           String value) {
        actionDto.setHttpStatus(400);
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setErrorMessage("Supplied value " + id + " is not available");
        actionDto.prepareResponseHeaders();
    }
    public interface HandleSecuredLoanActionDTO {

        // @formatter:off
        // Getters

        boolean isRequestingToRepay();
        boolean isOfferingShipForPawn();
        Visitor getQasinoVisitor();

        // Setters
        void setQasinoVisitor(Visitor visitor);

        // error setters
        void setHttpStatus(int status);
        void setErrorKey(String key);
        void setErrorValue(String value);
        void setErrorMessage(String key);
        void prepareResponseHeaders();
        // @formatter:on
    }
}
