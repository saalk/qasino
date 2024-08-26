package cloud.qasino.games.pattern.strategy.algorithm;

import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.database.entity.enums.card.PlayingCard;
import cloud.qasino.games.database.entity.enums.game.Type;
import cloud.qasino.games.database.entity.enums.move.Move;
import cloud.qasino.games.dto.model.CardDto;
import cloud.qasino.games.dto.model.GameDto;
import cloud.qasino.games.pattern.math.MathUtil;
import cloud.qasino.games.pattern.strategy.MovePredictor;
import cloud.qasino.games.pattern.strategy.NextMoveCalculator;
import cloud.qasino.games.pattern.stream.StreamUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
public class NormalMove extends NextMoveCalculator implements MovePredictor {
    @Override
    public Move predictMove(GameDto game) {

        // fallback to random move when the game has no cards yet
        if (game == null || game.getCards() == null) {
            log.info("NormalMove game.getCards is {}", game.getCards());
            Move move = new RandomMove().predictMove(game);
            log.info("NormalMove fallback game.getCards() == null to randomMove {}", move);
            return move;
        }

        List<CardDto> sortedCardsInHand = StreamUtil.sortCardsOnSequenceWithStream(game.getCards(), Location.HAND);
        Optional<CardDto> lastCardPlayed = StreamUtil.findLastCardInSortedList(sortedCardsInHand);
        log.info("NormalMove lastCardPlayed is {}", lastCardPlayed);

        // fallback to random move when the game has no cards yet
        if (lastCardPlayed.isEmpty()) {
            Move move = new RandomMove().predictMove(game);
            log.info("NormalMove fallback lastCardPlayed.isEmpty() to randomMove {}", move);
            return move;
        }

        int totalValueDeck = StreamUtil.countCardValuesOnRankAndSuit(game.getCards());
        double averageValueDeck = MathUtil.roundToNDigits(totalValueDeck / (double) game.getCards().size(), 1);
        int valuePreviousCard = PlayingCard.calculateValueWithDefaultHighlow(lastCardPlayed.get().getRankSuit(), Type.HIGHLOW);

        log.info("NormalMove averageValueDeck is {}", averageValueDeck);
        log.info("NormalMove valuePreviousCard is {}", valuePreviousCard);

        log.info("NormalMove is {}", (valuePreviousCard > averageValueDeck ? Move.LOWER : Move.HIGHER));

        return (valuePreviousCard > averageValueDeck ? Move.LOWER : Move.HIGHER);
    }

}
