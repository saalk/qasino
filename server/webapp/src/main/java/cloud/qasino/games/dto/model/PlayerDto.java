package cloud.qasino.games.dto.model;

import cloud.qasino.games.database.entity.enums.player.AiLevel;
import cloud.qasino.games.database.entity.enums.player.Avatar;
import cloud.qasino.games.database.entity.enums.player.PlayerType;
import lombok.Data;

@Data
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
    private int startFiches;
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


