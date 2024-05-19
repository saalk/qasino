package cloud.qasino.games.dto.enums;

import cloud.qasino.games.database.entity.enums.game.GameState;
import cloud.qasino.games.database.entity.enums.game.Type;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

@Getter
@ToString
public class GameEnums {

    public Map<String, Type> type = Type.typeMapNoError;
    StyleEnums style = new StyleEnums();
    Map<String, GameState> state = GameState.lookupNoError;

}
