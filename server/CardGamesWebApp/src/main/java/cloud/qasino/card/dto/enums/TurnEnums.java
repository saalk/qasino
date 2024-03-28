package cloud.qasino.card.dto.enums;

import cloud.qasino.card.database.entity.enums.move.Move;
import lombok.Getter;

import java.util.Map;

@Getter
public class TurnEnums {

    Map<String, Move> move = Move.moveMapNoError;

}
