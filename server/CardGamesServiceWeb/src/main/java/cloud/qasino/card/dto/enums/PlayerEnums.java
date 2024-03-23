package cloud.qasino.card.dto.enums;

import cloud.qasino.card.entity.enums.player.AiLevel;
import cloud.qasino.card.entity.enums.player.Avatar;
import cloud.qasino.card.entity.enums.player.Role;
import lombok.Getter;

import java.util.Map;

@Getter
public class PlayerEnums {

    Map<String, AiLevel> aiLevel = AiLevel.aiLevelMapNoError;
    Map<String, Avatar> avatar = Avatar.avatarMapNoError;
    Map<String, Role> role = Role.roleMapNoError;

}
