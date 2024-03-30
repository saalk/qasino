package cloud.qasino.games.dto;

import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.enums.player.AiLevel;
import cloud.qasino.games.database.entity.enums.player.Avatar;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.voodoodyne.jackson.jsog.JSOGGenerator;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonIdentityInfo(generator = JSOGGenerator.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Seat {

    private int seatId;
    private boolean isActive;

    private boolean isBot;
    private int playerId;
    private Avatar avatar;
    private AiLevel aiLevel;

    private boolean isInitiator;
    private int currentBet;

    // when visitor
    private int visitorId;
    private String visitorName;
    private int balance;
    private int fiches;

    private int activeTurnNumber;
    private boolean isWinner;

    private List<Card> cardsInHand = new ArrayList<>();
    private String stringCardsInHand;

}

