package cloud.qasino.card.domain.qasino;

import cloud.qasino.card.entity.enums.style.*;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public class Style {

    //HILOW_DRINKING_WITH_OPPONENTS(CardGame.HIGHLOW, "Drinking with opponent(s)"),
    // If wrong, s/he drinks once, twice or three times
    // After taking at least three cards, the player may choose to continue or
    // pass, BUT ONLY after having taken at least three cards. If the player
    // pass, the next player starts where the previous left off.
    // extra: The next player has to take a drink for each card the first player
    // won.

    //HILOW_LINEOFNINE(CardGame.HIGHLOW, "Line of Nine"),
    //HILOW_JACK(CardGame.HIGHLOW, "Teams of 2");
    // High Low Jack, also known as Hi Low Jack and Pitch, is played with a
    // standard 52-card playingCard. Partnership, the most commonly played version of
    // the game, places players in teams of two that score points collectively.
    // Varieties include Cutthroat, in which each player scores points
    // individually, and Nine Card, which deals three extra cards and awards
    // points for both the trump five and highest spade in play.

    /**
     * A static HashMap lookup with key + value is created to use in a getter
     * to fromLabel the Enum based on the name eg. key "Low" -> value AiLevel.DUMB
     */

    String label;

    MaxAnte maxAnte;
    BettingStrategy bettingStrategy;
    NumOfDecks numOfDecks;
    InsuranceCost insuranceCost;
    MaxRounds maxRounds;
    TurnsToWin turnsToWin;


    public Style() {

        this.label = "hraqn3";

        this.maxAnte = MaxAnte.HIGHEST_WINS;
        this.bettingStrategy = BettingStrategy.REGULAR;
        this.numOfDecks = NumOfDecks.ALL_CARDS;
        this.insuranceCost = InsuranceCost.QUARTER_ANTE;
        this.maxRounds = MaxRounds.NO_LIMIT;
        this.turnsToWin = TurnsToWin.THREE_IN_A_ROW_WINS;

    }

    public Style(String label) {
        this();

        Style style = Style.fromLabel(label);

        this.label = style.label;
        this.maxAnte = style.maxAnte;
        this.bettingStrategy = style.bettingStrategy;
        this.numOfDecks = style.numOfDecks;
        this.insuranceCost = style.insuranceCost;
        this.maxRounds = style.maxRounds;
        this.turnsToWin = style.turnsToWin;
    }

    static public Style fromLabel(String inputLabel) {

        Style style = new Style();
        String label = StringUtils.lowerCase(inputLabel);

        StringBuilder newLabel = new StringBuilder("      ");
        int len = label.length();

        if (len > 0) {
            char pos = label.charAt(1);
            style.maxAnte = MaxAnte.fromLabel(pos);
            char newPos = style.maxAnte.getLabel().charAt(1);
            newLabel.setCharAt(1,newPos);
        } else if (len > 1) {
            char pos = label.charAt(2);
            style.bettingStrategy = BettingStrategy.fromLabel(pos);
            char newPos = style.maxAnte.getLabel().charAt(2);
            newLabel.setCharAt(2,newPos);
        } else if (len > 2) {
            char pos = label.charAt(3);
            style.numOfDecks = NumOfDecks.fromLabel(pos);
            char newPos = style.numOfDecks.getLabel().charAt(3);
            newLabel.setCharAt(3,newPos);
        } else if (len > 3) {
            char pos = label.charAt(4);
            style.insuranceCost = InsuranceCost.fromLabel(pos);
            char newPos = style.insuranceCost.getLabel().charAt(4);
            newLabel.setCharAt(4,newPos);
        } else if (len > 4) {
            char pos = label.charAt(5);
            style.maxRounds = MaxRounds.fromLabel(pos);
            char newPos = style.maxRounds.getLabel().charAt(5);
            newLabel.setCharAt(5,newPos);
        } else if (len > 5) {
            char pos = label.charAt(6);
            style.turnsToWin = TurnsToWin.fromLabel(pos);
            char newPos = style.turnsToWin.getLabel().charAt(6);
            newLabel.setCharAt(6,newPos);
        }
        style.label = String.valueOf(newLabel);
        return style;
    }
}
