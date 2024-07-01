package cloud.qasino.games.pattern.strategy;

import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.Turn;
import cloud.qasino.games.database.entity.enums.move.Move;
import cloud.qasino.games.exception.MyNPException;
import cloud.qasino.games.pattern.strategy.algorithm.NormalMove;
import cloud.qasino.games.pattern.strategy.algorithm.RandomMove;
import cloud.qasino.games.pattern.strategy.algorithm.SmartMove;
import cloud.qasino.games.pattern.strategy.algorithm.StupidMove;


public class NextMoveCalculator {

    enum NextMove {StupidMove, RandomMove, NormalMove, SmartMove, PassMove}

    public static Move next(Game game, Player player, Turn turn) {

        // @formatter:off
        NextMove next = predict(game, player, turn);
        switch (next) {
            case StupidMove -> { return new StupidMove().predictMove(game);}
            case RandomMove -> { return new RandomMove().predictMove(game);}
            case NormalMove -> { return new NormalMove().predictMove(game);}
            case SmartMove  -> { return new SmartMove().predictMove(game);}
            case PassMove  -> { return Move.PASS;}
            default -> throw new MyNPException("NextMoveCalculator", "next [" + next + "]");
        }
        // @formatter:on
    }

    private static NextMove predict(Game game, Player player, Turn turn) {
        NextMove next = NextMove.RandomMove;
        switch (player.getAiLevel()) {
            case DUMB -> {
                if (turn.getCurrentMoveNumber() == 1) {
                    next = NextMove.StupidMove;
                } else if (turn.getCurrentMoveNumber() == 2) {
                    next = NextMove.RandomMove;
                } else {
                    next = NextMove.PassMove;
                }
            }
            case AVERAGE -> {
                if (turn.getCurrentMoveNumber() == 1) {
                    next = NextMove.RandomMove;
                } else if (turn.getCurrentMoveNumber() == 2) {
                    next = NextMove.NormalMove;
                } else {
                    next = NextMove.PassMove;
                }
            }
            case SMART -> {
                if (turn.getCurrentMoveNumber() == 1) {
                    next = NextMove.NormalMove;
                } else if (turn.getCurrentMoveNumber() == 2) {
                    next = NextMove.SmartMove;
                } else {
                    next = NextMove.PassMove;
                }
            }
        }
        return next;
    }
}