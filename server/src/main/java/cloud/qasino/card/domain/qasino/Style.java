package cloud.qasino.card.domain.qasino;

import cloud.qasino.card.domain.qasino.style.*;
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
    Deck deck;
    InsuranceCost insuranceCost;
    RoundsToWin roundsToWin;
    TurnsToWin turnsToWin;

    public Style() {
        this.label = "hr3tn3";
        this.maxAnte = MaxAnte.HIGHEST_WINS;
        this.bettingStrategy = BettingStrategy.REGULAR;
        this.deck = Deck.ALL_THREE_JOKER;
        this.insuranceCost = InsuranceCost.TENTH_ANTE;
        this.roundsToWin = RoundsToWin.NO_LIMIT;
        this.turnsToWin = TurnsToWin.THREE_IN_A_ROW_WINS;

    }

    public Style(String label, MaxAnte maxAnte, BettingStrategy bettingStrategy, Deck deck, InsuranceCost insuranceCost, RoundsToWin roundsToWin, TurnsToWin turnsToWin) {
        this.label = label;
        this.maxAnte = maxAnte;
        this.bettingStrategy = bettingStrategy;
        this.deck = deck;
        this.insuranceCost = insuranceCost;
        this.roundsToWin = roundsToWin;
        this.turnsToWin = turnsToWin;
    }

    static public Style fromLabelWithDefault(String inputLabel) {

        if (inputLabel.equals(null) || inputLabel.isEmpty()) {
            return new Style();
        }
        String label = StringUtils.lowerCase(inputLabel);

        MaxAnte maxAnte = MaxAnte.HIGHEST_WINS;
        BettingStrategy bettingStrategy = BettingStrategy.REGULAR;
        Deck deck = Deck.ALL_THREE_JOKER;
        InsuranceCost insuranceCost = InsuranceCost.TENTH_ANTE;
        RoundsToWin roundsToWin = RoundsToWin.NO_LIMIT;
        TurnsToWin turnsToWin = TurnsToWin.THREE_IN_A_ROW_WINS;

        StringBuilder newLabel = new StringBuilder("hr3tn3");
        final int len = label.length();
        char pos;
        char newPos;

        switch (len) {
            case 6:
                pos = label.charAt(5);
                turnsToWin = TurnsToWin.fromLabelWithDefault(pos);
                newPos = turnsToWin.getLabel().charAt(5);
                newLabel.setCharAt(5, newPos);

            case 5:
                pos = label.charAt(4);
                roundsToWin = RoundsToWin.fromLabelWithDefault(pos);
                newPos = roundsToWin.getLabel().charAt(4);
                newLabel.setCharAt(4, newPos);

            case 4:
                pos = label.charAt(3);
                insuranceCost = InsuranceCost.fromLabelWithDefault(pos);
                newPos = insuranceCost.getLabel().charAt(3);
                newLabel.setCharAt(3, newPos);

            case 3:
            pos = label.charAt(2);
            deck = Deck.fromLabelWithDefault(pos);
            newPos = deck.getLabel().charAt(2);
            newLabel.setCharAt(2, newPos);

            case 2:
                pos = label.charAt(1);
                bettingStrategy = BettingStrategy.fromLabelWithDefault(pos);
                newPos = bettingStrategy.getLabel().charAt(1);
                newLabel.setCharAt(1, newPos);

            case 1:
                pos = label.charAt(0);
                maxAnte = MaxAnte.fromLabelWithDefault(pos);
                newPos = maxAnte.getLabel().charAt(0);
                newLabel.setCharAt(0, newPos);
        }
        label = String.valueOf(newLabel);
        return new Style(label,maxAnte,bettingStrategy, deck,insuranceCost, roundsToWin,turnsToWin);
    }

    @Override
    public String toString() {
        return label;
    }
}
