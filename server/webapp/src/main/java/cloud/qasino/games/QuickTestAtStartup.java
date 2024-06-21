package cloud.qasino.games;

import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.CardMove;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.League;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.Turn;
import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.database.entity.enums.card.PlayingCard;
import cloud.qasino.games.database.entity.enums.game.GameState;
import cloud.qasino.games.database.entity.enums.move.Move;
import cloud.qasino.games.database.entity.enums.player.AiLevel;
import cloud.qasino.games.database.entity.enums.player.Avatar;
import cloud.qasino.games.database.entity.enums.player.PlayerType;
import cloud.qasino.games.database.repository.CardMoveRepository;
import cloud.qasino.games.database.repository.CardRepository;
import cloud.qasino.games.database.repository.GameRepository;
import cloud.qasino.games.database.repository.LeagueRepository;
import cloud.qasino.games.database.repository.PlayerRepository;
import cloud.qasino.games.database.repository.TurnRepository;
import cloud.qasino.games.database.security.Visitor;
import cloud.qasino.games.database.security.VisitorRepository;
import cloud.qasino.games.database.security.VisitorServiceOld;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Component
@Slf4j
public class QuickTestAtStartup implements ApplicationRunner {

    @Value("${spring.profiles.active:}")
    private String activeProfiles;

    @Autowired
    private VisitorServiceOld visitorServiceOld;

    @Autowired
    VisitorRepository visitorRepository;
    @Autowired
    LeagueRepository leagueRepository;
    @Autowired
    GameRepository gameRepository;
    @Autowired
    PlayerRepository playerRepository;
    @Autowired
    CardRepository cardRepository;
    @Autowired
    TurnRepository turnRepository;
    @Autowired
    CardMoveRepository cardMoveRepository;


    @Override
    public void run(ApplicationArguments args) throws Exception {
        String[] profiles = activeProfiles.split(",");
        log.warn("\nCurrently active profile - {} ", profiles[0]);

        String optionTest = "";
        if (args.getOptionValues("test") != null) {
            optionTest = args.getOptionValues("test").toString();
        }

        log.warn("\nApplication started with argument '--test=' is: \n {}\n",
                optionTest);
        if (profiles[0].equals("dev") && !optionTest.isEmpty() && !optionTest.equals("[skip]")) tests();
    }

    public void tests() {

        Random rand = new Random();
        int rand_int1 = rand.nextInt(100000);
        // A new visitor with 2 friends arrive
        Visitor visitor = Visitor.buildDummy("user1" + rand_int1, "User1" + rand_int1);
        Visitor friend1 = Visitor.buildDummy("friend1"+ rand_int1, "Friend1" + rand_int1);
        Visitor friend2 = Visitor.buildDummy("friend2"+ rand_int1, "Friend2" + rand_int1);

        int pawn = Visitor.pawnShipValue(0);
        visitor.pawnShip(pawn);
        visitor = visitorServiceOld.saveUser(visitor);

        pawn = Visitor.pawnShipValue(0);
        friend1.pawnShip(pawn);
        friend1 = visitorServiceOld.saveUser(friend1);

        pawn = Visitor.pawnShipValue(0);
        friend2.pawnShip(pawn);
        friend2 = visitorServiceOld.saveUser(friend2);

        // The visitor starts a league
        League league = League.buildDummy(visitor,"");
        league.endLeagueThisMonth();
        leagueRepository.save(league);

        // The qasino starts a NEW game in the league initiated by the visitor
        Game game = Game.buildDummy(league, visitor.getVisitorId());

        List<Card> cards = new ArrayList<>();
        List<PlayingCard> playingCards = PlayingCard.createDeckWithXJokers(0);
        Collections.shuffle(playingCards);
        int i = 1;
        for (PlayingCard playingCard : playingCards) {
            Card card = new Card(playingCard.getRankAndSuit(), game, null, i++, Location.STOCK);
            cards.add(card);
        }
        game.setCards(cards);
        game = gameRepository.save(game);
        cardRepository.saveAll(game.getCards()); // todo check this

        // The visitor initiates a game adding a bot also
        List<Player> visitorAndBot = new ArrayList<>();

        // seat 1 - human visitor
        visitorAndBot.add(playerRepository.save(Player.buildDummyHuman(visitor, game, Avatar.ELF)));
        // seat 2 - bot player with same fiches as visitor
        visitorAndBot.add(playerRepository.save(Player.buildDummyBot(game, Avatar.ELF, AiLevel.AVERAGE)));
        // seat 3 and 4 - Invite friend 1 and 2 to the game as player
        visitorAndBot.add(playerRepository.save(Player.buildDummyInvitee(visitor, game, Avatar.ELF)));
        game.setPlayers(visitorAndBot);
        game.setState(GameState.PENDING_INVITATIONS);

        // friend 1 and 2 accept the invitation
        List<Player> invites = new ArrayList<>();
        Pageable pageable = PageRequest.of(0, 4
//                Sort.by(
//                        Sort.Order.desc("created"))
        );
        invites = playerRepository.findAllPlayersInvitedForAGame(game.getGameId(), pageable);
        for (Player invite : invites) {
            if (invite.getPlayerType().equals(PlayerType.INVITED)) {
                invite.setPlayerType(PlayerType.INVITEE);
                playerRepository.save(invite);
            }
        }
        game.setState(GameState.PREPARED);
        gameRepository.save(game);

        // The main player initiates a turn with round 1 player 1 and gets a card
        Turn turn = new Turn(game, visitorAndBot.get(0).getPlayerId());
        turnRepository.save(turn);
        CardMove cardMove = new CardMove(turn, visitorAndBot.get(0), 0, Move.DEAL,
                Location.HAND, "details");
        cardMoveRepository.save(cardMove);
    }
}
