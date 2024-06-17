package cloud.qasino.games.pattern.strategy.algorithm;

import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.enums.move.Move;
import cloud.qasino.games.pattern.strategy.MoveMaker;
import cloud.qasino.games.pattern.strategy.NextMoveCalculator;

public class AverageMove extends NextMoveCalculator implements MoveMaker {
    @Override
    public Move calculate(Game game) {
        return Move.HIGHER;
    }

}
