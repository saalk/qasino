package cloud.qasino.games.simulator;

import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.League;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.Playing;
import cloud.qasino.games.database.entity.Result;
import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.database.entity.enums.card.PlayingCard;
import cloud.qasino.games.database.entity.enums.player.AiLevel;
import cloud.qasino.games.database.entity.enums.player.Avatar;
import cloud.qasino.games.database.security.MyUserPrincipal;
import cloud.qasino.games.database.security.Role;
import cloud.qasino.games.database.security.Visitor;
import cloud.qasino.games.dto.GameDto;
import cloud.qasino.games.dto.LeagueDto;
import cloud.qasino.games.dto.PlayerDto;
import cloud.qasino.games.dto.PlayingDto;
import cloud.qasino.games.dto.ResultDto;
import cloud.qasino.games.dto.VisitorDto;
import cloud.qasino.games.dto.mapper.GameMapper;
import cloud.qasino.games.dto.mapper.LeagueMapper;
import cloud.qasino.games.dto.mapper.PlayerMapper;
import cloud.qasino.games.dto.mapper.PlayingMapper;
import cloud.qasino.games.dto.mapper.VisitorMapper;
import cloud.qasino.games.pattern.factory.Deck;
import cloud.qasino.games.pattern.factory.DeckFactory;

import java.util.ArrayList;
import java.util.List;

public abstract class QasinoSimulator {

    // formatter:off
    public static int INITIATOR_5 = 5;
    public static Avatar HUMAN_AVATAR = Avatar.ELF;
    public static Avatar BOT_AVATAR = Avatar.GOBLIN;
    public static AiLevel BOT_AILEVEL = AiLevel.AVERAGE;

    public Visitor visitor;
    public MyUserPrincipal principal;
    public List<Role> visitorRoles;

    public VisitorDto visitorDto;

    public League league;
    public LeagueDto leagueDto;

    public Game game;
    public GameDto gameDto;
    public Game extraGame = Game.buildDummy(null, 1);
    public GameDto extraGameDto;
    public List<Game> games = new ArrayList<>();

    public Player playerVisitor;
    public PlayerDto playerVisitorDto;
    public Player bot;

    public Playing playing;
    public PlayingDto playingDto;

    public Result result;
    public ResultDto resultDto;

    public QasinoSimulator() {

        visitor = Visitor.buildDummy("username","alias");
        visitor.setVisitorId(INITIATOR_5);
        visitor.pawnShip(Visitor.pawnShipValue(0));
        principal = new MyUserPrincipal(visitor);
        visitorRoles = visitor.getRoles().stream().toList();

        // visitor
        visitorDto = VisitorMapper.INSTANCE.toDto(visitor);

        league = League.buildDummy(visitor,"topLeague");
        game = Game.buildDummy(league, INITIATOR_5);
        Deck deck = DeckFactory.createShuffledDeck(game, 2);
        List<PlayingCard> playingCards = deck.getPlayingCards();
        List<Card> cards = new ArrayList<>();
        int i = 1;
        for (PlayingCard playingCard : playingCards) {
            Card card = new Card(playingCard.getRankAndSuit(), game, null, i++, Location.STOCK);
            cards.add(card);
        }
        game.setCards(cards);
        extraGame.setCards(cards);
        games.add(game);
        games.add(extraGame);
        league.setGames(games);
        // league
        leagueDto = LeagueMapper.INSTANCE.toDto(league);

        playerVisitor = Player.buildDummyHuman(visitor, game, HUMAN_AVATAR);
        bot = Player.buildDummyBot(game, BOT_AVATAR, BOT_AILEVEL);
        List<Player> players = new ArrayList<>();
        players.add(playerVisitor);
        players.add(bot);
        cards.get(0).setHand(playerVisitor);
        cards.get(0).setLocation(Location.HAND);
        cards.get(1).setHand(bot);
        cards.get(1).setLocation(Location.HAND);
        // player
        playerVisitorDto = PlayerMapper.INSTANCE.toDto(playerVisitor, cards);

        game.setCards(cards);
        game.setPlayers(players);
        // game
        gameDto = GameMapper.INSTANCE.toDto(game, game.getCards());

        // TODO cardMoves

        playing = new Playing(game, playerVisitor);
        // playing
        playingDto = PlayingMapper.INSTANCE.toDto(playing);

        result = new Result(playerVisitor,visitor, game, game.getType(), 50,true);

    }
}
