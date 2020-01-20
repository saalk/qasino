package cloud.qasino.card.dto.enumrefs;

import cloud.qasino.card.entity.enums.player.AiLevel;
import cloud.qasino.card.entity.enums.player.Avatar;
import lombok.Getter;

import java.util.Map;

@Getter
public class PlayerEnums {

    Map<String, AiLevel> aiLevel = AiLevel.aiLevelMapNoError;
    Map<String, Avatar> avatar = Avatar.avatarMapNoError;

}
