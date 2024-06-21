package cloud.qasino.games.pattern.strategy;

import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.enums.move.Move;

public interface MovePredictor {
    Move predictMove(Game game);
}
