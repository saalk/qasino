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

    private List<Hand> hand;

    // FROM GAME - PLAYERS ordered by seat

    // seat stats
    private int seatId;
    private boolean isSeatPlaying;
    private boolean isSeatPlayerTheInitiator;
    private int seatStartBalance;
    private int seatFiches;
    private int seatCurrentBet;

    // player stats for the seat
    private long seatPlayerId;
    private PlayerType playerType;
    private Avatar avatar;
    private String avatarName;
    private AiLevel aiLevel;
    private boolean human;

    // when player for the seat is human
    private long visitorId;
    private String username;

    // cards in the hand of player for the seat
    private String allCardsInHand;
    private Card lastCardInHand;

}

