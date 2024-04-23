package cloud.qasino.games.action;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.entity.Visitor;
import cloud.qasino.games.database.repository.VisitorRepository;
import cloud.qasino.games.event.EventOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Random;

@Slf4j
@Component
public class UpdateTurnForGameType implements Action<UpdateTurnForGameType.UpdateTurnForGameTypeDTO, EventOutput.Result> {

    @Resource
    VisitorRepository visitorRepository;

    @Override
    public EventOutput.Result perform(UpdateTurnForGameTypeDTO actionDto) {

        Visitor updateVisitor = actionDto.getQasinoVisitor();

        if (actionDto.isRequestingToRepay()) {
            boolean repayOk = updateVisitor.repayLoan();
            if (!repayOk) {
                log.info("!repayOk");
                setErrorMessageConflict(actionDto, "Repay", "Repay loan with balance not possible, balance too low");
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
                setErrorMessageConflict(actionDto, "Pawn", "Ship already pawned, repay first");
                return EventOutput.Result.FAILURE;
            }
            actionDto.setQasinoVisitor(visitorRepository.save(updateVisitor));
            return EventOutput.Result.SUCCESS;
        }
        setErrorMessageBadRequest(actionDto, "Pawn or Repay", "Nothing to process");
        return EventOutput.Result.FAILURE;
    }

    void setErrorMessageConflict(UpdateTurnForGameTypeDTO actionDto, String id,
                                 String value) {
        actionDto.setHttpStatus(409);
        actionDto.setKey(id);
        actionDto.setValue(value);
        actionDto.setErrorMessage("Action [" + id + "] invalid");

    }
    private void setErrorMessageBadRequest(UpdateTurnForGameTypeDTO actionDto, String id,
                                           String value) {
        actionDto.setHttpStatus(500);
        actionDto.setKey(id);
        actionDto.setValue(value);
        actionDto.setErrorMessage("Action [" + id + "] invalid");

    }
    public interface UpdateTurnForGameTypeDTO {

        // @formatter:off
        // Getters

        boolean isRequestingToRepay();
        boolean isOfferingShipForPawn();
        Visitor getQasinoVisitor();

        // Setters
        void setQasinoVisitor(Visitor visitor);

        // error setters
        void setHttpStatus(int status);
        void setKey(String key);
        void setValue(String value);
        void setErrorMessage(String key);
        // @formatter:on
    }
}
