package cloud.qasino.games.response.view.enums;

import cloud.qasino.games.database.entity.enums.move.Move;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

@Getter
@ToString
public class GamingTableEnums {

    Map<String, Move> move = Move.moveMapNoError;

}
