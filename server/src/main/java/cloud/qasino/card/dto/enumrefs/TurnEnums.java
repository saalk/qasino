package cloud.qasino.card.dto.enumrefs;

import cloud.qasino.card.entity.enums.move.Move;
import lombok.Getter;

import java.util.Map;

@Getter
public class TurnEnums {

    Map<String, Move> move = Move.moveMapNoError;

}
