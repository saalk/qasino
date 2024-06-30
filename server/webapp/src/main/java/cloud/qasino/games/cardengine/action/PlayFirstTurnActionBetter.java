package cloud.qasino.games.cardengine.action;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.entity.Card;
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
import cloud.qasino.games.exception.MyNPException;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import cloud.qasino.games.pattern.statemachine.event.GameEvent;
import cloud.qasino.games.pattern.statemachine.event.TurnEvent;
import cloud.qasino.games.pattern.stream.StreamUtil;
import cloud.qasino.games.response.view.SectionTable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

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

        // First 1 move in HIGHLOW is move DEAL from location STOCK to location HAND with face UP
        Move firstDeal = Move.DEAL;
        Location fromLocation = Location.STOCK;
        Location toLocation = Location.HAND;
        Face face = Face.UP;
        int howMany = 1;

        if (!actionDto.getQasinoGame().getType().equals(Type.HIGHLOW)) {
            setBadRequestErrorMessage(actionDto, "Game Type", String.valueOf(actionDto.getQasinoGame().getType()));
            return EventOutput.Result.FAILURE;
        }

        Optional<Player> firstPlayer = StreamUtil.findFirstPlayerBySeat(actionDto.getQasinoGame().getPlayers());
        actionDto.setSuppliedTurnPlayerId(firstPlayer.get().getPlayerId());
        actionDto.setTurnPlayer(firstPlayer.get());

        Turn firstTurn = new Turn(actionDto.getQasinoGame(), actionDto.getTurnPlayer());
        Turn savedTurn = turnRepository.saveAndFlush(firstTurn);

        actionDto.setQasinoGame(gameRepository.getReferenceById(actionDto.getQasinoGame().getGameId()));
        actionDto.getQasinoGame().setTurn(savedTurn);
        if (actionDto.getQasinoGame().getTurn() == null) {
            throw new MyNPException("62 PlayFirstTurnAction", "gameId [" + actionDto.getQasinoGame().getGameId() + "]");
        }

        List<Card> cardsDealt = turnAndCardMoveService.dealNCardsFromStockToActivePlayerForGame(
                actionDto.getQasinoGame(),
                face,
                fromLocation,
                toLocation,
                howMany);
        List<CardMove> cardsMoved = turnAndCardMoveService.storeCardMovesForTurn(
                savedTurn,
                cardsDealt,
                firstDeal,
                toLocation);

        actionDto.setActiveTurn(firstTurn); // can be null
        actionDto.setAllCardMovesForTheGame(turnAndCardMoveService.getCardMovesForGame(actionDto.getQasinoGame())); // can be null

        return cardsDealt.isEmpty() ? EventOutput.Result.FAILURE : EventOutput.Result.SUCCESS;
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
