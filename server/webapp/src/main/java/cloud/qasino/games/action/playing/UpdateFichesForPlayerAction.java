package cloud.qasino.games.action.playing;

import cloud.qasino.games.action.dto.ActionDto;
import cloud.qasino.games.action.dto.Qasino;
import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.CardMove;
import cloud.qasino.games.database.entity.enums.card.PlayingCard;
import cloud.qasino.games.database.entity.enums.move.Move;
import cloud.qasino.games.database.repository.CardMoveRepository;
import cloud.qasino.games.database.repository.CardRepository;
import cloud.qasino.games.database.repository.PlayerRepository;
import cloud.qasino.games.database.service.PlayerService;
import cloud.qasino.games.database.service.PlayingService;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class UpdateFichesForPlayerAction extends ActionDto<EventOutput.Result> {

    // @formatter:off
    @Resource CardMoveRepository cardMoveRepository;
    @Resource CardRepository cardRepository;
    @Resource PlayerRepository playerRepository;
    @Autowired PlayingService playingService;
    @Autowired PlayerService playerService;
    // @formatter:on

    @Override
    public EventOutput.Result perform(Qasino qasino) {

        Optional<Card> previousCardMoveCard = Optional.of(new Card());
        CardMove previousCardMove;
        List<CardMove> cardMoves = cardMoveRepository.findByPlayerIdOrderBySequenceAsc(qasino.getPlaying().getCurrentPlayer().getPlayerId());
        for (CardMove cardMove : cardMoves) {
            switch (cardMove.getMove()) {
                case HIGHER, LOWER -> {
                    if (cardMove.getBet() == 0) {
                        // calculation needed
                        if (previousCardMoveCard.isEmpty()) {
                            setConflictErrorMessage(qasino, "Move", String.valueOf(cardMove.getMove()));
                            return EventOutput.Result.FAILURE;
                        }
                        updateWinOfLoss(qasino, cardMove, previousCardMoveCard.orElse(null));
                    }
                }
            }
            previousCardMove = cardMove;
            previousCardMoveCard = cardRepository.findById(cardMove.getCardId());
        }
        return EventOutput.Result.SUCCESS;
    }

    private void updateWinOfLoss(Qasino qasino, CardMove cardMove, Card previousCardMoveCard) {
        cardMove.setBet(qasino.getGame().getAnte());
        cardMove.setStartFiches(qasino.getPlaying().getCurrentPlayer().getFiches());
        Optional<Card> previousCard = cardRepository.findById(previousCardMoveCard.getCardId());
        Optional<Card> currentCard = cardRepository.findById(cardMove.getCardId());
        cardMove.setEndFiches(
                cardMove.getStartFiches() +
                        calculateWinOrLoss(qasino, cardMove.getMove(), previousCard.get(), currentCard.get()));
        cardMoveRepository.save(cardMove);
        qasino.getPlaying().setCurrentPlayer(playerService.updatePlayerFiches(qasino.getPlaying().getCurrentPlayer(), cardMove.getEndFiches()));
    }
    private int calculateWinOrLoss(Qasino qasino, Move move, Card previous, Card current) {
        int previousValue = PlayingCard.calculateValueWithDefaultHighlow(previous.getRankSuit(), qasino.getGame().getType());
        int currentValue = PlayingCard.calculateValueWithDefaultHighlow(current.getRankSuit(), qasino.getGame().getType());
        // calculate if the bet is added or subtracted
        if (previousValue == 0 || currentValue == 0) {
            // joker now or previous so ok you win the bet
            return qasino.getGame().getAnte();
        }
        if (previousValue == currentValue) {
            // with values being equal, you don't win or loose
            return 0;
        }
        if (move.equals(Move.HIGHER) && currentValue < previousValue) {
            // predicted higher and it is
            return qasino.getGame().getAnte();
        }
        if (move.equals(Move.LOWER) && currentValue > previousValue) {
            // predicted lower and it is
            return qasino.getGame().getAnte();
        }
        return qasino.getGame().getAnte();
    }

    void setErrorMessageConflictWithDeal(Qasino qasino, String id, String value) {
        qasino.getMessage().setErrorKey(id);
        qasino.getMessage().setErrorValue(value);
        qasino.getMessage().setConflictErrorMessage("Action [" + id + "] invalid, dealt card has a bet");
    }

    void setConflictErrorMessage(Qasino qasino, String id, String value) {
        qasino.getMessage().setErrorKey(id);
        qasino.getMessage().setErrorValue(value);
        qasino.getMessage().setConflictErrorMessage("Action [" + id + "] invalid, no previous card dealt");
    }

}