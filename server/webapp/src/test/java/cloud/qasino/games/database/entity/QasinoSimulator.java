package cloud.qasino.games.database.entity;

import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.database.entity.enums.move.Move;
import cloud.qasino.games.database.entity.enums.player.AiLevel;
import cloud.qasino.games.database.entity.enums.player.Role;

public abstract class QasinoSimulator {

    Visitor visitor;
    League league;
    Game game;
    Player player;
    Player bot;
    Turn turn;
    Result result;

    QasinoSimulator() {
        visitor = new Visitor("Julie", 1, "julie@domain.com");
        league = new League(visitor, "leagueName", 1);
        game = new Game(league, visitor.getVisitorId());
        game.shuffleGame(0);

        // add the visitor as player for the game
        player = new Player(visitor, game, Role.INITIATOR, 50, 1);
        // add a bot as player for the game
        bot = new Player(game, 50, 2, AiLevel.AVERAGE);

        // first turn
        turn = new Turn(game, player.getPlayerId());

        // assume visitor wins 50
        result = new Result(player,visitor, game,game.getType(), 50);

    }
}
