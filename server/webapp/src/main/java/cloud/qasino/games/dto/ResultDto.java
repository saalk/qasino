package cloud.qasino.games.dto;

import cloud.qasino.games.database.entity.enums.game.GameState;
import cloud.qasino.games.database.entity.enums.game.Type;
import cloud.qasino.games.database.entity.enums.player.AiLevel;
import cloud.qasino.games.database.entity.enums.player.Avatar;
import lombok.Data;

@Data
/**
 * The purpose of using this Dto is to separate the internal representation of user data
 * (e.g., in the database or business logic) from the data exposed to
 * external clients or systems.
 * */
public class ResultDto {

    private static final String NOT_BLANK_MESSAGE = "{notBlank.message}";
    private static final String EMAIL_MESSAGE = "{email.message}";

    long resultId;
    String name;

    long playerId;
    Avatar playerAvatar;
    String playerAvatarName;
    AiLevel playerAiLevel;

    long visitorId;
    String username;
    String alias;
    String aliasSequence;
    int balance;

    long gameId;
    GameState state;
    Type type;
    int ante;

    // derived
    boolean playerWinner;

}


