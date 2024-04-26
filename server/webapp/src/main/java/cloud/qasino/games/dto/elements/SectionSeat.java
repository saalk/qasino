package cloud.qasino.games.dto.elements;

import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.enums.player.AiLevel;
import cloud.qasino.games.database.entity.enums.player.Avatar;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.voodoodyne.jackson.jsog.JSOGGenerator;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonIdentityInfo(generator = JSOGGenerator.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SectionSeat {

    // seat stats
    @JsonProperty("SeatId")
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

    // player cards
    @JsonProperty("CardsInHand")
    private List<Card> cardsInHand = new ArrayList<>();
    @JsonProperty("StringCardsInHand")
    private String stringCardsInHand;

    // when player is human
    @JsonProperty("IsHumanPlayer")
    private boolean isHuman;
    @JsonProperty("VisitorId")
    private long visitorId;
    @JsonProperty("VisitorName")
    private String visitorName;

    // is player the winner
    @JsonProperty("IsWinner")
    private boolean isSeatWinner;



}

