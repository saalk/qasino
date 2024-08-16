package cloud.qasino.games.dto.model;

import cloud.qasino.games.database.entity.enums.player.AiLevel;
import cloud.qasino.games.database.entity.enums.player.Avatar;
import cloud.qasino.games.database.entity.enums.player.PlayerType;
import lombok.Data;

@Data
/**
 * The purpose of using this Dto is to separate the internal representation of user data
 * (e.g., in the database or business logic) from the data exposed to
 * external clients or systems.
 * */
public class PlayerDto {

    // core
    private long playerId;
//    private String created; // ignore

    // ref
    private VisitorDto visitor;
//    private GameDto game; // ignore as it will loop

    // Normal fields
    private int seat;
    private boolean human;
    private PlayerType playerType;
    private int fiches;
    private Avatar avatar;
    private String avatarName;
    private AiLevel aiLevel;

    private boolean winner;

//    private Playing playing; // ignore
//    private Result result; // ignore
//    private List<Card> cards; // ignore

    // derived
    private String cardsInHand;

}


