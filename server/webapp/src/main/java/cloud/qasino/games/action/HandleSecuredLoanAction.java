package cloud.qasino.games.action;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.security.Visitor;
import cloud.qasino.games.statemachine.event.EventOutput;
import cloud.qasino.games.database.security.VisitorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.Random;

@Slf4j
@Component
public class HandleSecuredLoanAction implements Action<HandleSecuredLoanAction.Dto, EventOutput.Result> {

    @Resource
    VisitorRepository visitorRepository;

    @Override
    public EventOutput.Result perform(Dto actionDto) {

        Visitor updateVisitor = actionDto.getQasinoVisitor();

        if (actionDto.isRequestingToRepay()) {
            boolean repayOk = updateVisitor.repayLoan();
            if (!repayOk) {
                log.info("!repayOk");
                setConflictErrorMessage(actionDto, "Repay", "Repay loan with balance not possible, balance too low");
                return EventOutput.Result.FAILURE;
            }
            actionDto.setQasinoVisitor(visitorRepository.save(updateVisitor));
            return EventOutput.Result.SUCCESS;
        }

        if (actionDto.isOfferingShipForPawn()) {
            Random random = new Random();
            int randomNumber = random.nextInt(1001);
            boolean pawnOk = updateVisitor.pawnShip(randomNumber);
            if (!pawnOk) {
                log.info("!pawnOk");
                setConflictErrorMessage(actionDto, "Pawn", "Ship already pawned, repay first");
                return EventOutput.Result.FAILURE;
            }
            actionDto.setQasinoVisitor(visitorRepository.save(updateVisitor));
            return EventOutput.Result.SUCCESS;
        }
        setBadRequestErrorMessage(actionDto, "Pawn or Repay", "Nothing to process");
        return EventOutput.Result.FAILURE;
    }

    private void setConflictErrorMessage(Dto actionDto, String id, String value) {
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setConflictErrorMessage("Action [" + id + "] invalid");

    }
    private void setBadRequestErrorMessage(Dto actionDto, String id, String value) {
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setBadRequestErrorMessage("Action [" + id + "] invalid");

    }
    public interface Dto {

        // @formatter:off
        // Getters

        boolean isRequestingToRepay();
        boolean isOfferingShipForPawn();
        Visitor getQasinoVisitor();

        // Setters
        void setQasinoVisitor(Visitor visitor);

        // error setters
        // @formatter:off
        void setBadRequestErrorMessage(String problem);
        void setNotFoundErrorMessage(String problem);
        void setConflictErrorMessage(String reason);
        void setUnprocessableErrorMessage(String reason);
        void setErrorKey(String errorKey);
        void setErrorValue(String errorValue);
        // @formatter:on
    }
}
