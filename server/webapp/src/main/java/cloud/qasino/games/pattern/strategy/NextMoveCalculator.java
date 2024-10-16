package cloud.qasino.games.pattern.strategy;

import cloud.qasino.games.database.entity.enums.move.Move;
import cloud.qasino.games.dto.model.GameDto;
import cloud.qasino.games.dto.model.PlayerDto;
import cloud.qasino.games.dto.model.PlayingDto;
import cloud.qasino.games.exception.MyNPException;
import cloud.qasino.games.pattern.strategy.algorithm.NormalMove;
import cloud.qasino.games.pattern.strategy.algorithm.RandomMove;
import cloud.qasino.games.pattern.strategy.algorithm.SmartMove;
import cloud.qasino.games.pattern.strategy.algorithm.StupidMove;


public class NextMoveCalculator {

    enum NextMove {StupidMove, RandomMove, NormalMove, SmartMove, PassMove}

    public static Move next(GameDto game, PlayerDto player, PlayingDto playing) {

        // @formatter:off
        NextMove next = predict(game, player, playing);
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

    private static NextMove predict(GameDto game, PlayerDto player, PlayingDto playing) {
        NextMove next = NextMove.RandomMove;
        switch (player.getAiLevel()) {
            case DUMB -> {
                if (playing.getCurrentMoveNumber() == 1) {
                    next = NextMove.StupidMove;
                } else if (playing.getCurrentMoveNumber() == 2) {
                } else {
                    next = NextMove.PassMove;
                }
            }
            case AVERAGE -> {
                if (playing.getCurrentMoveNumber() == 1) {
                } else if (playing.getCurrentMoveNumber() == 2) {
                    next = NextMove.NormalMove;
                } else {
                    next = NextMove.PassMove;
                }
            }
            case SMART -> {
                if (playing.getCurrentMoveNumber() == 1) {
                    next = NextMove.NormalMove;
                } else if (playing.getCurrentMoveNumber() == 2) {
                    next = NextMove.SmartMove;
                } else {
                    next = NextMove.PassMove;
                }
            }
        }
        return next;
    }
}