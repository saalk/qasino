package cloud.qasino.quiz.dto.enums;

import cloud.qasino.quiz.entity.enums.game.style.*;
import lombok.Getter;

import java.util.Map;

@Getter
public class StyleEnums {

    Map<String, AnteToWin> maxAnte = AnteToWin.lookup;
    Map<String, BettingStrategy> bettingStrategy = BettingStrategy.lookup;
    Map<String, Deck> deck = Deck.lookup;
    Map<String, InsuranceCost> insuranceCost = InsuranceCost.lookup;
    Map<String, RoundsToWin> roundsToWin = RoundsToWin.lookup;
    Map<String, TurnsToWin> turnsToWinMap = TurnsToWin.lookup;

}