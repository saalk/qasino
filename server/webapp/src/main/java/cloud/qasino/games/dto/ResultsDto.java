package cloud.qasino.games.dto;

import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.enums.player.AiLevel;
import cloud.qasino.games.database.entity.enums.player.Avatar;
import lombok.Data;

import java.util.List;

@Data
/**
 * The purpose of using this Dto is to separate the internal representation of user data
 * (e.g., in the database or business logic) from the data exposed to
 * external clients or systems.
 * */
public class ResultsDto {

    private static final String NOT_BLANK_MESSAGE = "{notBlank.message}";
    private static final String EMAIL_MESSAGE = "{email.message}";

    // for create and update
    long resultId;
    String name;

    long playerId;
    Avatar playerAvatar;
    String playerAvatarName;
    AiLevel playerAiLevel;

    // derived
    boolean playerWinner;

}


