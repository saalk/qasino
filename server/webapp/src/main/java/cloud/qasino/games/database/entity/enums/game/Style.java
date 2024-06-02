package cloud.qasino.games.database.entity.enums.game;

import cloud.qasino.games.database.entity.enums.game.style.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
public class Style {

    String label;

    AnteToWin anteToWin;
    BettingStrategy bettingStrategy;
    DeckConfiguration deckConfiguration;
    OneTimeInsurance oneTimeInsurance;
    RoundsToWin roundsToWin;
    TurnsToWin turnsToWin;

    public Style() {
        this.label = "nrrn22";
        this.anteToWin = AnteToWin.NA;
        this.bettingStrategy = BettingStrategy.REGULAR;
        this.deckConfiguration = DeckConfiguration.RANDOM_SUIT_NO_JOKER;
        this.oneTimeInsurance = OneTimeInsurance.NO;
        this.roundsToWin = RoundsToWin.TWO_ROUNDS;
        this.turnsToWin = TurnsToWin.TWO_IN_A_ROW_WINS;
    }

    public Style(String label, AnteToWin anteToWin, BettingStrategy bettingStrategy, DeckConfiguration deckConfiguration, OneTimeInsurance oneTimeInsurance, RoundsToWin roundsToWin, TurnsToWin turnsToWin) {
        this.label = label;
        this.anteToWin = anteToWin;
        this.bettingStrategy = bettingStrategy;
        this.deckConfiguration = deckConfiguration;
        this.oneTimeInsurance = oneTimeInsurance;
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
        DeckConfiguration deckConfiguration = DeckConfiguration.RANDOM_SUIT_NO_JOKER;
        OneTimeInsurance oneTimeInsurance = OneTimeInsurance.NO;
        RoundsToWin roundsToWin = RoundsToWin.TWO_ROUNDS;
        TurnsToWin turnsToWin = TurnsToWin.TWO_IN_A_ROW_WINS;

        StringBuilder newLabel = new StringBuilder("nrrn22");

        final int len = label.length();
        char pos;
        char newPos;

        // todo change to char array with loop
        switch (len) {
            case 6: // 6th pos is turnsToWin
                pos = label.charAt(5);
                turnsToWin = TurnsToWin.fromLabelWithDefault(pos);
                newPos = turnsToWin.getLabel().charAt(0);
                newLabel.setCharAt(5, newPos);

            case 5: // 5th pos is roundsToWin
                pos = label.charAt(4);
                roundsToWin = RoundsToWin.fromLabelWithDefault(pos);
                newPos = roundsToWin.getLabel().charAt(0);
                newLabel.setCharAt(4, newPos);

            case 4:
                pos = label.charAt(3);
                oneTimeInsurance = OneTimeInsurance.fromLabelWithDefault(pos);
                newPos = oneTimeInsurance.getLabel().charAt(0);
                newLabel.setCharAt(3, newPos);

            case 3:
                pos = label.charAt(2);
                deckConfiguration = DeckConfiguration.fromLabelWithDefault(pos);
                newPos = deckConfiguration.getLabel().charAt(0);
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
        return new Style(label, anteToWin, bettingStrategy, deckConfiguration, oneTimeInsurance, roundsToWin, turnsToWin);
    }

    public String updateLabelFromEnums() {
        StringBuilder newLabel = new StringBuilder(this.label);
        final int len = 6;
        char newPos;
        // todo change to char array with loop
        switch (len) {
            case 6: // 6th pos is turnsToWin
                newPos = this.turnsToWin.getLabel().charAt(0);
                newLabel.setCharAt(5, newPos);

            case 5: // 5th pos is roundsToWin
                newPos = this.roundsToWin.getLabel().charAt(0);
                newLabel.setCharAt(4, newPos);
            case 4:
                newPos = this.oneTimeInsurance.getLabel().charAt(0);
                newLabel.setCharAt(3, newPos);
            case 3:
                newPos = this.deckConfiguration.getLabel().charAt(0);
                newLabel.setCharAt(2, newPos);
            case 2:
                newPos = this.bettingStrategy.getLabel().charAt(0);
                newLabel.setCharAt(1, newPos);

            case 1:
                newPos = this.anteToWin.getLabel().charAt(0);
                newLabel.setCharAt(0, newPos);
        }
        return String.valueOf(newLabel);
    }

    @Override
    public String toString() {
        return "(" +
                "style=" + this.label +
                ", anteToWin=" + this.anteToWin +
                ", bettingStrategy=" + this.bettingStrategy +
                ", deckConfiguration=" + this.deckConfiguration +
                ", oneTimeInsurance=" + this.oneTimeInsurance +
                ", roundsToWin=" + this.roundsToWin +
                ", turnsToWin=" + this.turnsToWin +
                ")";
    }

}
