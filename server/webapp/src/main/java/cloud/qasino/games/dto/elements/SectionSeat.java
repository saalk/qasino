package cloud.qasino.games.dto.elements;

import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.CardMove;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.enums.player.AiLevel;
import cloud.qasino.games.database.entity.enums.player.Avatar;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SectionSeat {

    // seat stats
    @JsonProperty("SeatNumber")
    private int seatId;
    @JsonProperty("IsPlaying")
    private boolean isPlaying;
    @JsonProperty("IsPlayerIsInitiator")
    private boolean isSeatPlayerTheInitiator;

    // player stats
    @JsonProperty("PlayerId")
    private long seatPlayerId;
    @JsonProperty("Player")
    private Player seatPlayer;
    @JsonProperty("fiches")
    private int seatFiches;
    @JsonProperty("bet")
    private int seatCurrentBet;
    @JsonProperty("Avatar")
    private Avatar seatPlayerAvatar;
    @JsonProperty("AiLevel")
    private AiLevel seatPlayerAiLevel;

    // player cards and moves
//    @JsonIgnore
//    @JsonProperty("CardsInHand")
//    private List<Card> cardsInHand = new ArrayList<>();
    @JsonProperty("Hand")
    private String stringCardsInHand;
    @JsonProperty("PreviousCardMoves")
    private List<CardMove> previousCardMoves;

    // when player is human
    @JsonProperty("IsHumanPlayer")
    private boolean isHuman;
    @JsonProperty("VisitorId")
    private long visitorId;
    @JsonProperty("Username")
    private String username;

    // is player the winner
    @JsonProperty("IsWinner")
    private boolean isSeatWinner;


}

