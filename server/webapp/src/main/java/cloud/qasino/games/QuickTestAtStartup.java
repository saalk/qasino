package cloud.qasino.games;

import cloud.qasino.games.database.entity.*;
import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.database.entity.enums.move.Move;
import cloud.qasino.games.database.entity.enums.player.AiLevel;
import cloud.qasino.games.database.entity.enums.player.Avatar;
import cloud.qasino.games.database.entity.enums.player.Role;
import cloud.qasino.games.database.repository.*;
import cloud.qasino.games.statemachine.GameState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
        // A new VISITOR arrives
        Visitor visitor = new Visitor("visitorNameName",1 , "a@b.c");
        int pawn = Visitor.pawnShipValue(0);
        visitor.pawnShip(pawn);
        visitor = visitorRepository.save(visitor);

        // VISITOR starts a LEAGUE
        League league = new League(visitor, "default", 1);
        league.endLeagueThisMonth();
        leagueRepository.save(league);

        // The qasino starts a GAME
        Game game = new Game(league, "highlow", visitor.getVisitorId(), " ", 100);
        game.shuffleGame(0);
        game.setState(GameState.PLAYING);
        game = gameRepository.save(game);
        cardRepository.saveAll(game.getCards());

        // Add the VISITOR as a PLAYER to the GAME
        List<Player> players = new ArrayList<>();
        players.add(playerRepository.save(new Player(visitor, game, Role.INITIATOR,
                visitor.getBalance(), 1, Avatar.ELF, AiLevel.HUMAN)));
        // Add a bot player to the game
        players.add(playerRepository.save(new Player(null, game, Role.BOT, visitor.getBalance(), 2,
                Avatar.GOBLIN, AiLevel.AVERAGE)));
        game.setPlayers(players);

        // VISITOR initiates a TURN for the GAME
        Turn turn = new Turn(game, players.get(0).getPlayerId());
        turnRepository.save(turn);

        // VISITOR gets a card CARDMOVE
        CardMove cardMove = new CardMove(turn, players.get(0), 0, Move.DEAL,
                Location.HAND);
        cardMoveRepository.save(cardMove);
    }
}
