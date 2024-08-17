package cloud.qasino.games.response.enums;

import cloud.qasino.games.database.entity.enums.move.Move;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

@Getter
@ToString
public class PlayingEnums {

    Map<String, Move> move = Move.moveMapNoError;

}
