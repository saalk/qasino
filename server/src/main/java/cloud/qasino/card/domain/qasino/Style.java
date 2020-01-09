package cloud.qasino.card.domain.qasino;

import lombok.Getter;

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

    String style;

    cloud.qasino.card.entity.enums.style.MaxAnte maxAnte;
    cloud.qasino.card.entity.enums.style.BettingStrategy bettingStrategy;
    cloud.qasino.card.entity.enums.style.NumOfDecks numOfDecks;
    cloud.qasino.card.entity.enums.style.InsureanceCost insureanceCost;
    cloud.qasino.card.entity.enums.style.MaxRounds maxRounds;
    cloud.qasino.card.entity.enums.style.TurnsToWin turnsToWin;

    public Style() {

        fromStyle("");
    }

    public Style(String style) {
        this.style = style;
        fromStyle(style);
    }

    public void fromStyle(String style) {

        this.maxAnte = cloud.qasino.card.entity.enums.style.MaxAnte.HIGHEST_WINS;
        this.bettingStrategy = cloud.qasino.card.entity.enums.style.BettingStrategy.REGULAR;
        this.numOfDecks = cloud.qasino.card.entity.enums.style.NumOfDecks.ALL_CARDS;
        this.insureanceCost = cloud.qasino.card.entity.enums.style.InsureanceCost.QUARTER_ANTE;
        this.maxRounds = cloud.qasino.card.entity.enums.style.MaxRounds.NO_LIMIT;
        this.turnsToWin = cloud.qasino.card.entity.enums.style.TurnsToWin.THREE_IN_A_ROW_WINS;

        int len = style.length();
        if (len > 5) {
            this.turnsToWin = cloud.qasino.card.entity.enums.style.TurnsToWin.fromLabel(String.valueOf(style.charAt(6)));
        } else if (len > 4) {
            this.maxRounds = cloud.qasino.card.entity.enums.style.MaxRounds.fromLabel(String.valueOf(style.charAt(5)));
        } else if (len > 3) {
            this.insureanceCost = cloud.qasino.card.entity.enums.style.InsureanceCost.fromLabel(String.valueOf(style.charAt(4)));
        } else if (len > 2) {
            this.numOfDecks = cloud.qasino.card.entity.enums.style.NumOfDecks.fromLabel(String.valueOf(style.charAt(3)));
        } else if (len > 1) {
            this.bettingStrategy = cloud.qasino.card.entity.enums.style.BettingStrategy.fromLabel(String.valueOf(style.charAt(2)));
        } else if (len > 0) {
            this.maxAnte = cloud.qasino.card.entity.enums.style.MaxAnte.fromLabel(String.valueOf(style.charAt(1)));
        } else {
            this.style = "HRAQN3";
        }
    }

}
