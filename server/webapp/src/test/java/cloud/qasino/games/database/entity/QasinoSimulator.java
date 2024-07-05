package cloud.qasino.games.database.entity;

import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.database.entity.enums.card.PlayingCard;
import cloud.qasino.games.database.entity.enums.player.AiLevel;
import cloud.qasino.games.database.entity.enums.player.Avatar;
import cloud.qasino.games.database.security.Visitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class QasinoSimulator {


    Visitor visitor;
    League league;
    Game game;
    Player player;
    Player bot;
    Playing playing;
    Result result;

    QasinoSimulator() {
        visitor = Visitor.buildDummy("username","Alias");
        league = League.buildDummy(visitor,"leagueName");
        game = Game.buildDummy(league, visitor.getVisitorId());
        List<Card> cards = new ArrayList<>();
        List<PlayingCard> playingCards = PlayingCard.createDeckWithXJokers(0);
        Collections.shuffle(playingCards);
        int i = 1;
        for (PlayingCard playingCard : playingCards) {
            Card card = new Card(playingCard.getRankAndSuit(), game, null, i++, Location.STOCK);
            cards.add(card);
        }
        game.setCards(cards);

        // add the visitor as player for the game
        player = Player.buildDummyHuman(visitor, game, Avatar.ELF);

        // add a bot as player for the game
        bot = Player.buildDummyBot(game, Avatar.GOBLIN, AiLevel.AVERAGE);

        // first turn
        playing = new Playing(game, player);

        // assume visitor wins 50
        result = new Result(player,visitor, game,game.getType(), 50,true);

    }
}
