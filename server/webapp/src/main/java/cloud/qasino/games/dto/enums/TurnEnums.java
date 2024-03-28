package cloud.qasino.games.dto.enums;

import cloud.qasino.games.database.entity.enums.move.Move;
import lombok.Getter;

import java.util.Map;

@Getter
public class TurnEnums {

    Map<String, Move> move = Move.moveMapNoError;

}
