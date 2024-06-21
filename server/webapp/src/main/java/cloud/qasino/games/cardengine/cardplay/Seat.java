package cloud.qasino.games.cardengine.cardplay;

import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.enums.player.AiLevel;
import cloud.qasino.games.database.entity.enums.player.Avatar;
import cloud.qasino.games.database.entity.enums.player.PlayerType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Seat {

    // seat stats
    private int seatId;
    private boolean isSeatPlaying;
    private boolean isSeatPlayerTheInitiator;
    private int seatStartBalance;
    private int seatFiches;
    private int seatCurrentBet;

    // player stats
    private long seatPlayerId;
    private PlayerType playerType;
    private Avatar avatar;
    private String avatarName;
    private AiLevel aiLevel;
    private boolean human;

    // when player is human
    private long visitorId;
    private String username;

    private String allCardsInHand;
    private Card lastCardInHand;
    private List<Hand> hand;

}

