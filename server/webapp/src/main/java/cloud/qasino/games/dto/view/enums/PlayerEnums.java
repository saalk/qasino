package cloud.qasino.games.dto.view.enums;

import cloud.qasino.games.database.entity.enums.player.AiLevel;
import cloud.qasino.games.database.entity.enums.player.Avatar;
import cloud.qasino.games.database.entity.enums.player.Role;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

@Getter
@ToString
public class PlayerEnums {

    Map<String, AiLevel> aiLevel = AiLevel.aiLevelMapNoError;
    Map<String, Avatar> avatar = Avatar.avatarMapNoError;
    Map<String, Role> role = Role.roleMapNoError;

}
