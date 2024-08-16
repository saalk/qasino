package cloud.qasino.games.simulator;

import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.CardMove;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.League;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.Playing;
import cloud.qasino.games.database.entity.Result;
import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.database.entity.enums.card.PlayingCard;
import cloud.qasino.games.database.entity.enums.move.Move;
import cloud.qasino.games.database.entity.enums.player.AiLevel;
import cloud.qasino.games.database.entity.enums.player.Avatar;
import cloud.qasino.games.database.security.MyUserPrincipal;
import cloud.qasino.games.database.security.Visitor;
import cloud.qasino.games.dto.model.GameDto;
import cloud.qasino.games.dto.model.LeagueDto;
import cloud.qasino.games.dto.model.PlayerDto;
import cloud.qasino.games.dto.model.PlayingDto;
import cloud.qasino.games.dto.model.ResultDto;
import cloud.qasino.games.dto.model.RoleDto;
import cloud.qasino.games.dto.model.SeatDto;
import cloud.qasino.games.dto.model.VisitorDto;
import cloud.qasino.games.dto.mapper.GameMapper;
import cloud.qasino.games.dto.mapper.LeagueMapper;
import cloud.qasino.games.dto.mapper.PlayerMapper;
import cloud.qasino.games.dto.mapper.PlayingMapper;
import cloud.qasino.games.dto.mapper.ResultMapper;
import cloud.qasino.games.dto.mapper.RoleMapper;
import cloud.qasino.games.dto.mapper.SeatMapper;
import cloud.qasino.games.dto.mapper.VisitorMapper;
import cloud.qasino.games.pattern.factory.Deck;
import cloud.qasino.games.pattern.factory.DeckFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class QasinoSimulator {

    // formatter:off
    public static int INITIATOR_5 = 5;
    public static Avatar HUMAN_AVATAR = Avatar.ELF;
    public static Avatar BOT_AVATAR = Avatar.GOBLIN;
    public static AiLevel BOT_AILEVEL = AiLevel.AVERAGE;

    public Visitor visitor;
    public MyUserPrincipal principal;
    public List<RoleDto> visitorRoles;

    public VisitorDto visitorDto;

    public League league;
    public LeagueDto leagueDto;

    public Game game;
    public Game extraGame = Game.buildDummy(null, 1);
    public GameDto gameDto;
    public GameDto extraGameDto;
    public List<Game> games = new ArrayList<>();

    public Player playerVisitor;
    public PlayerDto playerVisitorDto;
    public Player bot;

    public Playing playing;
    public PlayingDto playingDto;
    public SeatDto playerVisitorSeatDto;
    public SeatDto botSeatDto;
    public List<SeatDto> seatDtos = new ArrayList<>();

    public Result playerVisitorResult;
    public Result botResult;
    public ResultDto playerVisitorResultDto;
    public ResultDto botResultDto;
    public List<ResultDto> resultDtos = new ArrayList<>();


    public QasinoSimulator() {

        visitor = Visitor.buildDummy("username", "alias");
        visitor.setVisitorId(INITIATOR_5);
        visitor.pawnShip(Visitor.pawnShipValue(0));
        principal = new MyUserPrincipal(visitor);
        visitorRoles = RoleMapper.INSTANCE.toDtoList(visitor.getRoles().stream().toList());

        // visitor
        visitorDto = VisitorMapper.INSTANCE.toDto(visitor);

        league = League.buildDummy(visitor, "topLeague");
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
        cards.get(1).setHand(playerVisitor);
        cards.get(1).setLocation(Location.HAND);

        cards.get(2).setHand(bot);
        cards.get(2).setLocation(Location.HAND);
        cards.get(3).setHand(bot);
        cards.get(3).setLocation(Location.HAND);

        cards.get(4).setHand(playerVisitor);
        cards.get(4).setLocation(Location.HAND);
        cards.get(5).setHand(playerVisitor);
        cards.get(5).setLocation(Location.HAND);
        // player
        playerVisitorDto = PlayerMapper.INSTANCE.toDto(playerVisitor, cards);

        game.setCards(cards);
        game.setPlayers(players);
        // game
        gameDto = GameMapper.INSTANCE.toDto(game, game.getCards());

        playing = new Playing(game, playerVisitor);
//        log.warn("playing data <{}>", playing);

        List<CardMove> cardMoves = new ArrayList<>();
        for (Card card : cards) {
            if (card.getHand() == null) break;
            Move move = Move.ERROR;
            int round = 0;
            int seat = 0;
            int turn = 0;
            switch (card.getSequence()) {
                case 1: {
                    move = Move.DEAL;
                    round = 1;
                    seat = 1;
                    turn = 1;

                }
                case 2: {
                    move = Move.HIGHER;
                    round = 1;
                    seat = 1;
                    turn = 2;
                }
                case 3: {
                    move = Move.DEAL;
                    round = 1;
                    seat = 2;
                    turn = 1;
                }
                case 4: {
                    move = Move.HIGHER;
                    round = 1;
                    seat = 2;
                    turn = 2;
                }
                case 5: {
                    move = Move.DEAL;
                    round = 2;
                    seat = 1;
                    turn = 1;
                }
                case 6: {
                    move = Move.LOWER;
                    round = 2;
                    seat = 1;
                    turn = 2;
                }
            }
            CardMove cardMove;
            if (card.getHand().getPlayerId() == playerVisitor.getPlayerId()) {
                cardMove = new CardMove(playing, playerVisitor, card.getCardId(), move, card.getLocation(), card.getRankSuit());
            } else {
                cardMove = new CardMove(playing, bot, card.getCardId(), move, card.getLocation(), card.getRankSuit());
            }
            cardMoves.add(cardMove);

        }

        playing.setCardMoves(cardMoves);
        // playing
        playingDto = PlayingMapper.INSTANCE.toDto(playing);

        // seats
        playerVisitorSeatDto = SeatMapper.INSTANCE.toDto(playerVisitor, playing);
        seatDtos.add(playerVisitorSeatDto);
        botSeatDto = SeatMapper.INSTANCE.toDto(bot, playing);
        seatDtos.add(botSeatDto);

        // results
        playerVisitorResult = new Result(playerVisitor, visitor, game, game.getType(), 50, true);
        playerVisitorResultDto = ResultMapper.INSTANCE.toDto(playerVisitorResult);
        botResult = new Result(bot, null, game, game.getType(), 40, false);
        botResultDto = ResultMapper.INSTANCE.toDto(botResult);
        resultDtos.add(playerVisitorResultDto);

        resultDtos.add(botResultDto);


    }
}
