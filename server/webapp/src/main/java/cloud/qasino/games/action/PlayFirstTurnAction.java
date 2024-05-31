package cloud.qasino.games.action;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.entity.CardMove;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.Turn;
import cloud.qasino.games.database.entity.enums.card.Face;
import cloud.qasino.games.database.entity.enums.game.Type;
import cloud.qasino.games.database.entity.enums.move.Move;
import cloud.qasino.games.database.service.TurnAndCardMoveService;
import cloud.qasino.games.dto.elements.SectionTable;
import cloud.qasino.games.statemachine.event.EventOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class PlayFirstTurnAction implements Action<PlayFirstTurnAction.Dto, EventOutput.Result> {

    @Autowired
    TurnAndCardMoveService turnAndCardMoveService;

    @Override
    public EventOutput.Result perform(Dto actionDto) {

        if (!actionDto.getQasinoGame().getType().equals(Type.HIGHLOW)) {
            setBadRequestErrorMessage(actionDto, "Game Type", String.valueOf(actionDto.getQasinoGame().getType()));
            return EventOutput.Result.FAILURE;
        }

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
        Turn activeTurn = turnAndCardMoveService.dealCardToPlayer(
                actionDto.getQasinoGame(),
                null,
                actionDto.getTurnPlayer(),
                Move.DEAL,
                Face.UP,
                1);
        actionDto.setActiveTurn(activeTurn); // can be null
        actionDto.setSuppliedTurnPlayerId(activeTurn.getActivePlayerId()); // can be null
        actionDto.setAllCardMovesForTheGame(turnAndCardMoveService.getCardMovesForGame(actionDto.getQasinoGame())); // can be null

        return EventOutput.Result.SUCCESS;
    }

    private void setBadRequestErrorMessage(Dto actionDto, String id, String value) {
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setBadRequestErrorMessage("Action [" + id + "] invalid - only highlow implemented");
    }

    public interface Dto {

        // @formatter:off
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
