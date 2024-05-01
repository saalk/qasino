package cloud.qasino.games.dto.enums;

import cloud.qasino.games.database.entity.enums.game.style.*;
import lombok.Getter;

import java.util.Map;

@Getter
public class StyleEnums {

    Map<String, AnteToWin> maxAnte = AnteToWin.lookup;
    Map<String, BettingStrategy> bettingStrategy = BettingStrategy.lookup;
    Map<String, DeckConfiguration> deck = DeckConfiguration.lookup;
    Map<String, OneTimeInsurance> insuranceCost = OneTimeInsurance.lookup;
    Map<String, RoundsToWin> roundsToWin = RoundsToWin.lookup;
    Map<String, TurnsToWin> turnsToWinMap = TurnsToWin.lookup;

}
