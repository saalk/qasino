package cloud.qasino.games.action;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Visitor;
import cloud.qasino.games.database.repository.VisitorRepository;
import cloud.qasino.games.event.EventOutput;
import cloud.qasino.games.statemachine.trigger.GameTrigger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class MakeGamePlayableForGameType implements Action<MakeGamePlayableForGameType.MakeGamePlayableForGameTypeDTO, EventOutput.Result> {

    @Resource
    VisitorRepository visitorRepository;

    @Override
    public EventOutput.Result perform(MakeGamePlayableForGameTypeDTO actionDto) {

        Game updateGame = actionDto.getQasinoGame();
//        boolean repayOk = updateGame.;
        return EventOutput.Result.FAILURE;
    }

    private void setErrorMessageBadRequest(MakeGamePlayableForGameTypeDTO actionDto, String id,
                                           String value) {
        actionDto.setHttpStatus(500);
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setErrorMessage("Action [" + id + "] invalid");
    }

    public interface MakeGamePlayableForGameTypeDTO {

        // @formatter:off
        // Getters
        Visitor getQasinoGamePlayers();
        GameTrigger getSuppliedGameTrigger();
        Game getQasinoGame();

        // error setters
        void setHttpStatus(int status);
        void setErrorKey(String errorKey);
        void setErrorValue(String errorValue);
        void setErrorMessage(String key);
        // @formatter:on
    }
}
