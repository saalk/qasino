package cloud.qasino.card.entity;

import cloud.qasino.card.entity.enums.variant.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
public class GameVariant {

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
    // standard 52-card deck. Partnership, the most commonly played version of
    // the game, places players in teams of two that score points collectively.
    // Varieties include Cutthroat, in which each player scores points
    // individually, and Nine Card, which deals three extra cards and awards
    // points for both the trump five and highest spade in play.

    /**
     * A static HashMap lookup with key + value is created to use in a getter
     * to fromLabel the Enum based on the name eg. key "Low" -> value AiLevel.DUMB
     */

    String variant;

    VariantAnte variantAnte;
    VariantBetting variantBetting;
    VariantDeck variantDeck;
    VariantInsurance variantInsurance;
    VariantRound variantRound;
    VariantTurns variantTurns;

    public GameVariant() {
        fromVariant("");
    }

    public GameVariant(String variant) {
        this.variant = variant;
        fromVariant(variant);
    }

    public void fromVariant(String variant) {

        this.variantAnte = VariantAnte.HIGHEST_WINS;
        this.variantBetting = VariantBetting.REGULAR;
        this.variantDeck = VariantDeck.ALL_CARDS;
        this.variantInsurance = VariantInsurance.NO;
        this.variantRound = VariantRound.NO_LIMIT;
        this.variantTurns = VariantTurns.NO_LIMIT;

        int len = variant.length();
        if (len > 5) {
            this.variantTurns = VariantTurns.fromLabel(String.valueOf(variant.charAt(6)));
        } else if (len > 4) {
            this.variantRound = VariantRound.fromLabel(String.valueOf(variant.charAt(5)));
        } else if (len > 3) {
            this.variantInsurance = VariantInsurance.fromLabel(String.valueOf(variant.charAt(4)));
        } else if (len > 2) {
            this.variantDeck = VariantDeck.fromLabel(String.valueOf(variant.charAt(3)));
        } else if (len > 1) {
            this.variantBetting = VariantBetting.fromLabel(String.valueOf(variant.charAt(2)));
        } else if (len > 0) {
            this.variantAnte = VariantAnte.fromLabel(String.valueOf(variant.charAt(1)));
        } else {
            this.variant = "HRANNN";
        }
    }

}
