package cloud.qasino.games.action;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.CardMove;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.Turn;
import cloud.qasino.games.database.entity.enums.card.Face;
import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.database.entity.enums.move.Move;
import cloud.qasino.games.database.repository.CardMoveRepository;
import cloud.qasino.games.database.repository.CardRepository;
import cloud.qasino.games.database.repository.TurnRepository;
import cloud.qasino.games.event.EventOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class PlayNextTurnAndCardMovesForHuman implements Action<PlayNextTurnAndCardMovesForHuman.Dto, EventOutput.Result> {

    @Resource
    CardRepository cardRepository;
    TurnRepository turnRepository;
    CardMoveRepository cardMoveRepository;

    @Override
    public EventOutput.Result perform(Dto actionDto) {

        // POST - turntrigger HIGER|LOWER|PASS for visitor
        // -> gamestate CASHED

        Optional<Player> player1 =
                actionDto.getQasinoGamePlayers()
                        .stream()
                        .filter(p -> p.getSeat() == 1)
                        .findFirst();
        if (player1.isEmpty()) {
            setErrorMessageInternalServerError(actionDto, "Qasino.Player(s).seat", String.valueOf(actionDto.getQasinoGame().getSeats()));
            return EventOutput.Result.FAILURE;
        }
        actionDto.setActiveTurn(new Turn(
                actionDto.getQasinoGame(),
                player1.get().getPlayerId()));
        turnRepository.save(actionDto.getActiveTurn());

        CardMove cardMove = new CardMove(actionDto.getActiveTurn(), actionDto.getTurnPlayer(), 0, Move.DEAL,
                Location.HAND);
        cardMoveRepository.save(cardMove);
        Card firstCard = actionDto.getCardsInTheGameSorted().get(0);
        firstCard.setLocation(Location.HAND);
        firstCard.setFace(Face.UP);
        firstCard.setHand(player1.get());
        cardRepository.save(firstCard);
        return EventOutput.Result.SUCCESS;
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
        Game getQasinoGame();

        void setQasinoGame(Game game);
        void setActiveTurn(Turn turn);
        Turn getActiveTurn();
        Player getTurnPlayer();
        List<Card> getCardsInTheGameSorted();

        // error setters
        void setHttpStatus(int status);
        void setErrorKey(String errorKey);
        void setErrorValue(String errorValue);
        void setErrorMessage(String key);
        // @formatter:on
    }
}
