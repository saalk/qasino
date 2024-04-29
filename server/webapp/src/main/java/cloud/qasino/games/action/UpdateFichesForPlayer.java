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
public class UpdateFichesForPlayer implements Action<UpdateFichesForPlayer.Dto, EventOutput.Result> {

    @Resource
    VisitorRepository visitorRepository;

    @Override
    public EventOutput.Result perform(Dto actionDto) {

        return EventOutput.Result.SUCCESS;
    }

    void setErrorMessageConflict(Dto actionDto, String id,
                                 String value) {
        actionDto.setHttpStatus(409);
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setErrorMessage("Action [" + id + "] invalid");

    }
    private void setErrorMessageBadRequest(Dto actionDto, String id,
                                           String value) {
        actionDto.setHttpStatus(500);
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setErrorMessage("Action [" + id + "] invalid");

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
        void setHttpStatus(int status);
        void setErrorKey(String errorKey);
        void setErrorValue(String errorValue);
        void setErrorMessage(String key);
        // @formatter:on
    }
}
