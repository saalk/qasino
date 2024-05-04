package cloud.qasino.games.action;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.enums.game.Style;
import cloud.qasino.games.database.repository.CardRepository;
import cloud.qasino.games.database.repository.GameRepository;
import cloud.qasino.games.database.service.PlayService;
import cloud.qasino.games.event.EventOutput;
import cloud.qasino.games.statemachine.trigger.GameTrigger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Component
public class PlayGameForType implements Action<PlayGameForType.Dto, EventOutput.Result> {

    @Autowired
    PlayService playService;

    @Override
    public EventOutput.Result perform(Dto actionDto) {

        Style style = Style.fromLabelWithDefault(actionDto.getQasinoGame().getStyle());
        int jokers = 0;
        switch(style.getDeckConfiguration()) {
            case ALL_THREE_JOKER -> {
                jokers = 3;
                break;
            }
            case ALL_TWO_JOKER -> {
                jokers = 2;
                break;
            }
            case ALL_ONE_JOKER -> {
                jokers = 1;
                break;
            }
            case ALL_NO_JOKERS -> {
            }
            case RANDOM_SUIT -> {
                break;
            }
            default -> throw new IllegalStateException("Unexpected value: " + style.getDeckConfiguration());
        }
        // update Game, create Cards for game, create Turn, create CardMove
        actionDto.setQasinoGame(playService.addCardsToGame(actionDto.getQasinoGame(), jokers));
        return EventOutput.Result.SUCCESS;
    }

    public interface Dto {

        // @formatter:off
        // Getters
        List<Player> getQasinoGamePlayers();
        GameTrigger getSuppliedGameTrigger();
        Game getQasinoGame();

        void setQasinoGame(Game game);

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
