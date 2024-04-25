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
public class MapTableAndSeatsForGame implements Action<MapTableAndSeatsForGame.MapTableAndSeatsForGameDTO, EventOutput.Result> {

    @Resource
    VisitorRepository visitorRepository;

    @Override
    public EventOutput.Result perform(MapTableAndSeatsForGameDTO actionDto) {

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

    void setErrorMessageConflict(MapTableAndSeatsForGameDTO actionDto, String id,
                                 String value) {
        actionDto.setHttpStatus(409);
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setErrorMessage("Action [" + id + "] invalid");

    }
    private void setErrorMessageBadRequest(MapTableAndSeatsForGameDTO actionDto, String id,
                                           String value) {
        actionDto.setHttpStatus(500);
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setErrorMessage("Action [" + id + "] invalid");

    }
    public interface MapTableAndSeatsForGameDTO {

        // @formatter:off
        // Getters

        boolean isRequestingToRepay();
        boolean isOfferingShipForPawn();
        Visitor getQasinoVisitor();

        // Setters
        void setQasinoVisitor(Visitor visitor);

        // error setters
        void setHttpStatus(int status);
        void setErrorKey(String errorKey);
        void setErrorValue(String errorValue);
        void setErrorMessage(String key);
        // @formatter:on
    }
}
