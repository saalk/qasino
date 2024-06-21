package cloud.qasino.games.cardengine.action;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.entity.CardMove;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.Turn;
import cloud.qasino.games.database.entity.enums.card.Face;
import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.database.entity.enums.game.Type;
import cloud.qasino.games.database.entity.enums.move.Move;
import cloud.qasino.games.database.repository.GameRepository;
import cloud.qasino.games.database.repository.TurnRepository;
import cloud.qasino.games.database.service.TurnAndCardMoveService;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import cloud.qasino.games.pattern.statemachine.event.GameEvent;
import cloud.qasino.games.pattern.statemachine.event.TurnEvent;
import cloud.qasino.games.response.view.SectionTable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class PlayFirstTurnActionBetter implements Action<PlayFirstTurnActionBetter.Dto, EventOutput.Result> {

    @Autowired
    TurnAndCardMoveService turnAndCardMoveService;
    @Autowired
    private TurnRepository turnRepository;
    @Autowired
    private GameRepository gameRepository;

    @Override
    public EventOutput.Result perform(Dto actionDto) {

        if (!actionDto.getQasinoGame().getType().equals(Type.HIGHLOW)) {
            setBadRequestErrorMessage(actionDto, "Game Type", String.valueOf(actionDto.getQasinoGame().getType()));
            return EventOutput.Result.FAILURE;
        }

        // 1. make a turn, find the first player
        if (actionDto.getSuppliedTurnPlayerId() == 0) {
            actionDto.setSuppliedTurnPlayerId(
                    actionDto.getQasinoGamePlayers()
                            .stream()
                            .filter(p -> p.getSeat() == 1)
                            .findFirst().get().getPlayerId());
            actionDto.setTurnPlayer(
                    actionDto.getQasinoGamePlayers()
                            .stream()
                            .filter(p -> p.getSeat() == 1)
                            .findFirst().get());
        }
        Turn firstTurn = new Turn(actionDto.getQasinoGame(), actionDto.getTurnPlayer().getPlayerId());
        Turn updatedTurn = turnRepository.save(firstTurn);
        actionDto.getQasinoGame().setTurn(updatedTurn);
        boolean cardDealt = turnAndCardMoveService.dealCardToPlayer(
                actionDto.getQasinoGame(),
                Move.DEAL,
                Face.UP,
                Location.HAND,
                1);
        actionDto.setActiveTurn(firstTurn); // can be null
        actionDto.setAllCardMovesForTheGame(turnAndCardMoveService.getCardMovesForGame(actionDto.getQasinoGame())); // can be null

        return cardDealt ? EventOutput.Result.SUCCESS : EventOutput.Result.FAILURE;
    }

    private void setBadRequestErrorMessage(Dto actionDto, String id, String value) {
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setBadRequestErrorMessage("Action [" + id + "] invalid - only highlow implemented");
    }

    public interface Dto {

        // @formatter:off
        String getErrorMessage();
        GameEvent getSuppliedGameEvent();
        TurnEvent getSuppliedTurnEvent();

        // Getters
        List<Player> getQasinoGamePlayers();
        Game getQasinoGame();
        SectionTable getTable();

        void setQasinoGame(Game game);
        void setActiveTurn(Turn turn);
        Turn getActiveTurn();
        Player getTurnPlayer();
        void setTurnPlayer(Player turnPlayer);
        long getSuppliedTurnPlayerId();
        void setSuppliedTurnPlayerId(long turnPlayerId);
        void setAllCardMovesForTheGame(List<CardMove> cardMoves);

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
