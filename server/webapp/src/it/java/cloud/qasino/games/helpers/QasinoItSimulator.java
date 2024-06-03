package cloud.qasino.games.helpers;

import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.League;
import cloud.qasino.games.database.security.Visitor;
import cloud.qasino.games.database.entity.enums.game.Style;
import cloud.qasino.games.database.entity.enums.game.Type;
import cloud.qasino.games.database.entity.enums.game.style.AnteToWin;
import cloud.qasino.games.database.entity.enums.game.style.BettingStrategy;
import cloud.qasino.games.database.entity.enums.game.style.DeckConfiguration;
import cloud.qasino.games.database.entity.enums.game.style.OneTimeInsurance;
import cloud.qasino.games.database.entity.enums.game.style.RoundsToWin;
import cloud.qasino.games.database.entity.enums.game.style.TurnsToWin;
import cloud.qasino.games.database.entity.enums.move.Move;

import java.util.EnumSet;
import java.util.Set;

import static cloud.qasino.games.database.entity.enums.move.Move.HIGHER;
import static cloud.qasino.games.database.entity.enums.move.Move.LOWER;
import static cloud.qasino.games.database.entity.enums.move.Move.PASS;

public abstract class QasinoItSimulator {

    Visitor visitor;
    League league;

    QasinoItSimulator() {
        visitor = new Visitor.Builder()
                .withUsername("itTester")
                .withPassword("itTester")
                .withEmail("itTester@domain.com")
                .withAlias("itTester")
                .withAliasSequence(1)
                .build();

        league = new League(visitor, "itLeague", 1);

        // Game setup
        Type type = Type.HIGHLOW;
        String label = "nr3tnn";
        AnteToWin anteToWin = AnteToWin.NA;
        BettingStrategy bettingStrategy = BettingStrategy.REGULAR;
        DeckConfiguration deckConfiguration = DeckConfiguration.ALL_THREE_JOKERS;
        OneTimeInsurance oneTimeInsurance = OneTimeInsurance.TENTH_ANTE;
        RoundsToWin roundsToWin = RoundsToWin.NA;
        TurnsToWin turnsToWin = TurnsToWin.NA;
        Style style = new Style(label, anteToWin, bettingStrategy, deckConfiguration, oneTimeInsurance,
                roundsToWin, turnsToWin);
        int ante = 50;

        Game game = new Game(league, type.getLabel(), visitor.getVisitorId(), style.getLabel(),
                ante);

        // play
        Set<Move> possibleMoves = EnumSet.of(HIGHER, LOWER, PASS);

    }
}
