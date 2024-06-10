package cloud.qasino.games.dto.view.enums;

import cloud.qasino.games.database.entity.enums.game.style.*;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class StyleEnums {

    List<AnteToWin> anteToWinList = List.of(AnteToWin.values());
    List<BettingStrategy> bettingStrategyList = List.of(BettingStrategy.values());
    List<DeckConfiguration> deckConfigurationList = List.of(DeckConfiguration.values());
    List<OneTimeInsurance> oneTimeInsuranceList = List.of(OneTimeInsurance.values());
    List<RoundsToWin> roundsToWinList = List.of(RoundsToWin.values());
    List<TurnsToWin> turnsToWinList = List.of(TurnsToWin.values());

}
