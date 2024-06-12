package cloud.qasino.games.dto;

import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Result;
import cloud.qasino.games.database.entity.enums.player.AiLevel;
import cloud.qasino.games.database.entity.enums.player.Avatar;
import cloud.qasino.games.database.entity.enums.player.PlayerState;
import lombok.Data;

@Data
/**
 * The purpose of using this Dto is to separate the internal representation of user data
 * (e.g., in the database or business logic) from the data exposed to
 * external clients or systems.
 * */
public class PlayerDto {

    private static final String NOT_BLANK_MESSAGE = "{notBlank.message}";
    private static final String EMAIL_MESSAGE = "{email.message}";

    // for create and update
    private long playerId;
    private long visitorId;
    private Game game;
    private PlayerState playerState;
    private int fiches;
    private Avatar avatar;
    private String avatarName;
    private AiLevel aiLevel;

    // derived
    private boolean human;
    private boolean winner;

    // for view
    private int seat;
    private String stringCardsInHand;
    Result result;

}


