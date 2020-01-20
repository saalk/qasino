package cloud.qasino.card.dto.enumrefs;

import cloud.qasino.card.domain.qasino.style.*;
import lombok.Getter;

import java.util.Map;

@Getter
public class StyleEnums {

    Map<String, MaxAnte> maxAnte = MaxAnte.lookup;
    Map<String, BettingStrategy> bettingStrategy = BettingStrategy.lookup;
    Map<String, Deck> deck = Deck.lookup;
    Map<String, InsuranceCost> insuranceCost = InsuranceCost.lookup;
    Map<String, RoundsToWin> roundsToWin = RoundsToWin.lookup;
    Map<String, TurnsToWin> turnsToWinMap = TurnsToWin.lookup;

}
