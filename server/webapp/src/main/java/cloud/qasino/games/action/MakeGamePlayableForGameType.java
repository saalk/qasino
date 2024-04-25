package cloud.qasino.games.action;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.entity.CardMove;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.Turn;
import cloud.qasino.games.database.entity.Visitor;
import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.database.entity.enums.game.GameState;
import cloud.qasino.games.database.entity.enums.game.Style;
import cloud.qasino.games.database.entity.enums.move.Move;
import cloud.qasino.games.database.repository.CardMoveRepository;
import cloud.qasino.games.database.repository.CardRepository;
import cloud.qasino.games.database.repository.GameRepository;
import cloud.qasino.games.database.repository.TurnRepository;
import cloud.qasino.games.event.EventOutput;
import cloud.qasino.games.statemachine.trigger.GameTrigger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class MakeGamePlayableForGameType implements Action<MakeGamePlayableForGameType.Dto, EventOutput.Result> {

    @Resource
    GameRepository gameRepository;
    CardRepository cardRepository;
    TurnRepository turnRepository;
    CardMoveRepository cardMoveRepository;

    @Override
    public EventOutput.Result perform(Dto actionDto) {

        Style style = Style.fromLabelWithDefault(actionDto.getQasinoGame().getStyle());
        int jokers = 0;
        switch(style.getDeck()) {
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
            default -> throw new IllegalStateException("Unexpected value: " + style.getDeck());
        }
        // update Game, create Cards for game, create Turn, create CardMove
        actionDto.getQasinoGame().shuffleGame(jokers);
        actionDto.getQasinoGame().setState(GameState.INITIALIZED);

        Optional<Player> player =
                actionDto.getQasinoGamePlayers()
                        .stream()
                        .filter(p -> p.getSeat() == 1)
                        .findFirst();
        if (player.isEmpty()) {
            setErrorMessageInternalServerError(actionDto, "Qasino.Player(s).seat", String.valueOf(actionDto.getQasinoGame().getSeats()));
            return EventOutput.Result.FAILURE;
        }
        gameRepository.save(actionDto.getQasinoGame());
        cardRepository.saveAll(actionDto.getQasinoGame().getCards());
        actionDto.setActiveTurn(new Turn(
                actionDto.getQasinoGame(),
                player.get().getPlayerId()));
        turnRepository.save(actionDto.getActiveTurn());

        CardMove cardMove = new CardMove(actionDto.getActiveTurn(), actionDto.getTurnPlayer(), 0, Move.DEAL,
                Location.HAND);
        cardMoveRepository.save(cardMove);
        // todo card

        return EventOutput.Result.FAILURE;
    }

    private void setErrorMessageInternalServerError(Dto actionDto, String id,
                                                    String value) {
        actionDto.setHttpStatus(500);
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setErrorMessage("Action [" + id + "] invalid - no seat with 1");
    }

    public interface Dto {

        // @formatter:off
        // Getters
        List<Player> getQasinoGamePlayers();
        GameTrigger getSuppliedGameTrigger();
        Game getQasinoGame();

        void setQasinoGame(Game game);
        void setActiveTurn(Turn turn);
        Turn getActiveTurn();
        Player getTurnPlayer();
        void setTurnPlayer(Player player);

        // error setters
        void setHttpStatus(int status);
        void setErrorKey(String errorKey);
        void setErrorValue(String errorValue);
        void setErrorMessage(String key);
        // @formatter:on
    }
}
