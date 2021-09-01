package cloud.qasino.quiz.dto.enums;

import cloud.qasino.quiz.entity.enums.move.Move;
import lombok.Getter;

import java.util.Map;

@Getter
public class TurnEnums {

    Map<String, Move> move = Move.moveMapNoError;

}
