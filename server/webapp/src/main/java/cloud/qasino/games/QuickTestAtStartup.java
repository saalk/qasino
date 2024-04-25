package cloud.qasino.games;

import cloud.qasino.games.database.entity.*;
import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.database.entity.enums.move.Move;
import cloud.qasino.games.database.entity.enums.player.AiLevel;
import cloud.qasino.games.database.entity.enums.player.Avatar;
import cloud.qasino.games.database.entity.enums.player.Role;
import cloud.qasino.games.database.repository.*;
import cloud.qasino.games.database.entity.enums.game.GameState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class QuickTestAtStartup implements ApplicationRunner {

    @Value("${spring.profiles.active:}")
    private String activeProfiles;

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
        log.info("\nCurrently active profile - {} ", profiles[0]);

        String optionTest = "";
        if (args.getOptionValues("test") != null) {
            optionTest = args.getOptionValues("test").toString();
        }

        log.info("\nApplication started with argument '--test=' is: \n {}\n",
                optionTest);
        if (profiles[0].equals("dev") && !optionTest.isEmpty() && !optionTest.equals("[skip]")) tests();
    }

    public void tests() {

        // A new visitor with 2 friends arrive
        Visitor visitor = new Visitor("visitorName",1 , "a@a.c");
        int pawn = Visitor.pawnShipValue(0);
        visitor.pawnShip(pawn);
        visitor = visitorRepository.save(visitor);

        Visitor friend1 = new Visitor("friendName",1 , "a@b.c");
        pawn = Visitor.pawnShipValue(0);
        friend1.pawnShip(pawn);
        friend1 = visitorRepository.save(visitor);

        Visitor friend2 = new Visitor("friendName",2 , "b@b.c");
        pawn = Visitor.pawnShipValue(0);
        friend2.pawnShip(pawn);
        friend2 = visitorRepository.save(visitor);

        // The visitor starts a league
        League league = new League(visitor, "defaultLeague", 1);
        league.endLeagueThisMonth();
        leagueRepository.save(league);

        // The qasino starts a NEW game in the league initiated by the visitor
        Game game = new Game(league, "highlow", visitor.getVisitorId(), " ", 100);
        game.shuffleGame(0);
        game.setState(GameState.INITIALIZED);
        game = gameRepository.save(game);
        cardRepository.saveAll(game.getCards()); // todo check this

        // The visitor initiates a game adding a bot also
        List<Player> visitorAndBot = new ArrayList<>();
        // seat 1 - human visitor player with role initiator
        visitorAndBot.add(playerRepository.save(new Player(visitor, game, Role.INITIATOR,
                visitor.getBalance(), 1, Avatar.ELF, AiLevel.HUMAN)));
        // seat 2 - bot player with same fiches as visitor
        visitorAndBot.add(playerRepository.save(new Player(null, game, Role.BOT, visitor.getBalance(), 2,
                Avatar.GOBLIN, AiLevel.AVERAGE)));
        // The visitor invites 2 friends also
        // seat 3 and 4 - Invite friend 1 and 2 to the game as player
        visitorAndBot.add(playerRepository.save(new Player(friend1, game, Role.INVITED, friend1.getBalance(), 3,
                Avatar.GOBLIN, AiLevel.HUMAN)));
        visitorAndBot.add(playerRepository.save(new Player(friend2, game, Role.INVITED, friend2.getBalance(), 4,
                Avatar.GOBLIN, AiLevel.HUMAN)));
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
            if (invite.getRole().equals(Role.INVITED)) {
                invite.setRole(Role.ACCEPTED);
                playerRepository.save(invite);
            }
        }
        game.setState(GameState.PREPARED);
        gameRepository.save(game);

        // The main player initiates a turn with round 1 player 1 and gets a card
        Turn turn = new Turn(game, visitorAndBot.get(0).getPlayerId());
        turnRepository.save(turn);
        CardMove cardMove = new CardMove(turn, visitorAndBot.get(0), 0, Move.DEAL,
                Location.HAND);
        cardMoveRepository.save(cardMove);
    }
}
