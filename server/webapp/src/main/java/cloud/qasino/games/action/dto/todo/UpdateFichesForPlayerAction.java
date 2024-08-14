package cloud.qasino.games.action.dto.todo;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.CardMove;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.enums.card.PlayingCard;
import cloud.qasino.games.database.entity.enums.move.Move;
import cloud.qasino.games.database.repository.CardMoveRepository;
import cloud.qasino.games.database.repository.CardRepository;
import cloud.qasino.games.database.repository.PlayerRepository;
import cloud.qasino.games.database.service.PlayingService;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import cloud.qasino.games.pattern.statemachine.event.GameEvent;
import cloud.qasino.games.pattern.statemachine.event.PlayEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class UpdateFichesForPlayerAction implements Action<UpdateFichesForPlayerAction.Dto, EventOutput.Result> {

    @Resource
    CardMoveRepository cardMoveRepository;
    @Resource
    CardRepository cardRepository;
    @Resource
    PlayerRepository playerRepository;

    @Autowired
    PlayingService playingService;

    @Override
    public EventOutput.Result perform(Dto actionDto) {

        Optional<Card> previousCardMoveCard = Optional.of(new Card());
        CardMove previousCardMove;
        List<CardMove> cardMoves = cardMoveRepository.findByPlayerIdOrderBySequenceAsc(actionDto.getPlayingPlayer().getPlayerId());
        for (CardMove cardMove : cardMoves) {
            switch (cardMove.getMove()) {
                case HIGHER, LOWER -> {
                    if (cardMove.getBet() == 0) {
                        // calculation needed
                        if (previousCardMoveCard.isEmpty()) {
                            setConflictErrorMessage(actionDto, "Move", String.valueOf(cardMove.getMove()));
                            return EventOutput.Result.FAILURE;
                        }
                        updateWinOfLoss(actionDto, cardMove, previousCardMoveCard.orElse(null));
                    }
                }
            }
            previousCardMove = cardMove;
            previousCardMoveCard = cardRepository.findById(cardMove.getCardId());
        }
        return EventOutput.Result.SUCCESS;
    }

    private void updateWinOfLoss(Dto actionDto, CardMove cardMove, Card previousCardMoveCard) {
        cardMove.setBet(actionDto.getQasinoGame().getAnte());
        cardMove.setStartFiches(actionDto.getPlayingPlayer().getFiches());
        Optional<Card> previousCard = cardRepository.findById(previousCardMoveCard.getCardId());
        Optional<Card> currentCard = cardRepository.findById(cardMove.getCardId());
        cardMove.setEndFiches(
                cardMove.getStartFiches() +
                        calculateWinOrLoss(actionDto, cardMove.getMove(), previousCard.get(), currentCard.get()));
        cardMoveRepository.save(cardMove);
        actionDto.getPlayingPlayer().setFiches(cardMove.getEndFiches());
        playerRepository.save(actionDto.getPlayingPlayer());
        actionDto.setAllCardMovesForTheGame(playingService.findCardMovesForGame(actionDto.getQasinoGame())); // can be null
    }
    private int calculateWinOrLoss(Dto actionDto, Move move, Card previous, Card current) {
        int previousValue = PlayingCard.calculateValueWithDefaultHighlow(previous.getRankSuit(), actionDto.getQasinoGame().getType());
        int currentValue = PlayingCard.calculateValueWithDefaultHighlow(current.getRankSuit(), actionDto.getQasinoGame().getType());
        // calculate if the bet is added or subtracted
        if (previousValue == 0 || currentValue == 0) {
            // joker now or previous so ok you win the bet
            return -actionDto.getQasinoGame().getAnte();
        }
        if (previousValue == currentValue) {
            // with values being equal, you don't win or loose
            return 0;
        }
        if (move.equals(Move.HIGHER) && currentValue < previousValue) {
            // predicted higher and it is
            return -actionDto.getQasinoGame().getAnte();
        }
        if (move.equals(Move.LOWER) && currentValue > previousValue) {
            // predicted lower and it is
            return -actionDto.getQasinoGame().getAnte();
        }
        return actionDto.getQasinoGame().getAnte();
    }

    void setErrorMessageConflictWithDeal(Dto actionDto, String id, String value) {
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setConflictErrorMessage("Action [" + id + "] invalid, dealt card has a bet");
    }

    void setConflictErrorMessage(Dto actionDto, String id, String value) {
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setConflictErrorMessage("Action [" + id + "] invalid, no previous card dealt");
    }

    public interface Dto {

        // @formatter:off
        String getErrorMessage();
        GameEvent getSuppliedGameEvent();
        PlayEvent getSuppliedPlayEvent();

        // Getters
        List<CardMove> getAllCardMovesForTheGame();
        Game getQasinoGame();
        Player getPlayingPlayer();

        // Setters
        void setPlayingPlayer(Player player);
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
