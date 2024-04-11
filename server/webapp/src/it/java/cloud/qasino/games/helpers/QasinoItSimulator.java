package cloud.qasino.games.helpers;

import cloud.qasino.games.database.entity.*;
import cloud.qasino.games.database.entity.enums.game.Style;
import cloud.qasino.games.database.entity.enums.game.Type;
import cloud.qasino.games.database.entity.enums.game.style.*;
import cloud.qasino.games.database.entity.enums.move.Move;
import cloud.qasino.games.database.entity.enums.player.AiLevel;
import cloud.qasino.games.database.entity.enums.player.Role;

import java.util.EnumSet;
import java.util.Set;

import static cloud.qasino.games.database.entity.enums.move.Move.*;

public abstract class QasinoItSimulator {

    Visitor visitor;
    League league;

    QasinoItSimulator() {
        visitor = new Visitor("itTester", 1, "tester@domain.com");
        league = new League(visitor, "itLeague", 1);

        // Game setup
        Type type = Type.HIGHLOW;
        String label = "nr3tnn";
        AnteToWin anteToWin = AnteToWin.NA;
        BettingStrategy bettingStrategy = BettingStrategy.REGULAR;
        Deck deck = Deck.ALL_THREE_JOKER;
        InsuranceCost insuranceCost = InsuranceCost.TENTH_ANTE;
        RoundsToWin roundsToWin = RoundsToWin.NA;
        TurnsToWin turnsToWin = TurnsToWin.NA;
        Style style = new Style(label, anteToWin, bettingStrategy, deck, insuranceCost,
                roundsToWin, turnsToWin);
        int ante = 50;

        Game game = new Game(league, type.getLabel(), visitor.getVisitorId(), style.getLabel(),
                ante);

        // play
        Set<Move> possibleMoves = EnumSet.of(HIGHER, LOWER, PASS);

    }
}
