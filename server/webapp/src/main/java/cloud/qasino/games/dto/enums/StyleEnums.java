package cloud.qasino.games.dto.enums;

import cloud.qasino.games.database.entity.enums.game.style.*;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

@Getter
@ToString
public class StyleEnums {

    Map<String, AnteToWin> anteToWin = AnteToWin.lookup;
    Map<String, BettingStrategy> bettingStrategy = BettingStrategy.lookup;
    Map<String, DeckConfiguration> deckConfiguration = DeckConfiguration.lookup;
    Map<String, OneTimeInsurance> oneTimeInsurance = OneTimeInsurance.lookup;
    Map<String, RoundsToWin> roundsToWin = RoundsToWin.lookup;
    Map<String, TurnsToWin> turnsToWinMap = TurnsToWin.lookup;

}
