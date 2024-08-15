package cloud.qasino.games.pattern.strategy.algorithm;

import cloud.qasino.games.database.entity.enums.move.Move;
import cloud.qasino.games.dto.GameDto;
import cloud.qasino.games.pattern.strategy.MovePredictor;
import cloud.qasino.games.pattern.strategy.NextMoveCalculator;

public class RandomMove extends NextMoveCalculator implements MovePredictor {
    @Override
    public Move predictMove(GameDto game) {
        return (Math.random() < 0.5 ? Move.LOWER : Move.HIGHER);
    }
}
