package cloud.qasino.games.pattern.strategy;

import cloud.qasino.games.database.entity.enums.move.Move;
import cloud.qasino.games.dto.model.GameDto;

public interface MovePredictor {
    Move predictMove(GameDto game);
}
