package cloud.qasino.games.pattern.strategy.algorithm;

import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.enums.move.Move;
import cloud.qasino.games.pattern.strategy.MovePredictor;
import cloud.qasino.games.pattern.strategy.NextMoveCalculator;

public class RandomMove extends NextMoveCalculator implements MovePredictor {
    @Override
    public Move predictMove(Game game) {
        return (Math.random() < 0.5 ? Move.LOWER : Move.HIGHER);
    }
}
