package cloud.qasino.quiz.entity.enums.game;

import cloud.qasino.quiz.entity.enums.game.style.*;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public class Style {

    String label;

    AnteToWin anteToWin;
    BettingStrategy bettingStrategy;
    Deck deck;
    InsuranceCost insuranceCost;
    RoundsToWin roundsToWin;
    TurnsToWin turnsToWin;

    public Style() {
        this.label = "nr3tnn";
        this.anteToWin = AnteToWin.NA;
        this.bettingStrategy = BettingStrategy.REGULAR;
        this.deck = Deck.ALL_THREE_JOKER;
        this.insuranceCost = InsuranceCost.TENTH_ANTE;
        this.roundsToWin = RoundsToWin.NA;
        this.turnsToWin = TurnsToWin.NA;

    }

    public Style(String label, AnteToWin anteToWin, BettingStrategy bettingStrategy, Deck deck, InsuranceCost insuranceCost, RoundsToWin roundsToWin, TurnsToWin turnsToWin) {
        this.label = label;
        this.anteToWin = anteToWin;
        this.bettingStrategy = bettingStrategy;
        this.deck = deck;
        this.insuranceCost = insuranceCost;
        this.roundsToWin = roundsToWin;
        this.turnsToWin = turnsToWin;
    }

    static public Style fromLabelWithDefault(String inputLabel) {

        if (inputLabel == null || inputLabel.isEmpty()) {
            return new Style();
        }
        String label = StringUtils.lowerCase(inputLabel);

        AnteToWin anteToWin = AnteToWin.NA;
        BettingStrategy bettingStrategy = BettingStrategy.REGULAR;
        Deck deck = Deck.ALL_THREE_JOKER;
        InsuranceCost insuranceCost = InsuranceCost.TENTH_ANTE;
        RoundsToWin roundsToWin = RoundsToWin.NA;
        TurnsToWin turnsToWin = TurnsToWin.NA;

        StringBuilder newLabel = new StringBuilder("hr3tn3");

        final int len = label.length();
        char pos;
        char newPos;

        // todo change to char array with loop
        switch (len) {
            case 6:
                pos = label.charAt(5);
                turnsToWin = TurnsToWin.fromLabelWithDefault(pos);
                newPos = turnsToWin.getLabel().charAt(0);
                newLabel.setCharAt(5, newPos);

            case 5:
                pos = label.charAt(4);
                roundsToWin = RoundsToWin.fromLabelWithDefault(pos);
                newPos = roundsToWin.getLabel().charAt(0);
                newLabel.setCharAt(4, newPos);

            case 4:
                pos = label.charAt(3);
                insuranceCost = InsuranceCost.fromLabelWithDefault(pos);
                newPos = insuranceCost.getLabel().charAt(0);
                newLabel.setCharAt(3, newPos);

            case 3:
                pos = label.charAt(2);
                deck = Deck.fromLabelWithDefault(pos);
                newPos = deck.getLabel().charAt(0);
                newLabel.setCharAt(2, newPos);

            case 2:
                pos = label.charAt(1);
                bettingStrategy = BettingStrategy.fromLabelWithDefault(pos);
                newPos = bettingStrategy.getLabel().charAt(0);
                newLabel.setCharAt(1, newPos);

            case 1:
                pos = label.charAt(0);
                anteToWin = AnteToWin.fromLabelWithDefault(pos);
                newPos = anteToWin.getLabel().charAt(0);
                newLabel.setCharAt(0, newPos);
        }
        label = String.valueOf(newLabel);
        return new Style(label, anteToWin, bettingStrategy, deck, insuranceCost, roundsToWin, turnsToWin);
    }

    @Override
    public String toString() {
        return label;
    }
}
