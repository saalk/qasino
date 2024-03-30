package cloud.qasino.games.configuration;

import cloud.qasino.games.database.entity.CardMove;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.League;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.Turn;
import cloud.qasino.games.database.entity.Visitor;
import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.database.entity.enums.move.Move;
import cloud.qasino.games.database.entity.enums.player.AiLevel;
import cloud.qasino.games.database.entity.enums.player.Avatar;
import cloud.qasino.games.database.entity.enums.player.Role;
import cloud.qasino.games.database.repository.CardMoveRepository;
import cloud.qasino.games.database.repository.CardRepository;
import cloud.qasino.games.database.repository.GameRepository;
import cloud.qasino.games.database.repository.LeagueRepository;
import cloud.qasino.games.database.repository.PlayerRepository;
import cloud.qasino.games.database.repository.TurnRepository;
import cloud.qasino.games.database.repository.VisitorRepository;
import cloud.qasino.games.statemachine.GameState;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

//import org.springframework.security.oauth2.config.annotation.web.configuration
// .EnableResourceServer;

/**
 * In spring framework bean declared in ApplicationContext.xml can reside in five scopes:
 * <p>
 * common beans with ApplicationContext:
 * 1) Singleton (default scope) - you always get the same bean
 * 2) prototype - you will get a new instance of the spring bean
 * <p>
 * only for web aware applications with WebApplicationContext:
 * 3) request - each http request has its own bean
 * 4) session - each session has its own bean
 * 5) global-session
 */

@SpringBootApplication
@EnableAutoConfiguration
//@EnableResourceServer //
@EnableJpaRepositories(basePackages = "cloud.qasino.games.database.*")
@EnableTransactionManagement
@EntityScan(basePackages = "cloud.qasino.games.*")
@EnableJpaAuditing
@Slf4j
public class GamesApplication {
    @Value("${spring.profiles.active:dev}")
    private static String activeProfile; // dev = default

    public static void main(String[] args) {
        SpringApplication.run(GamesApplication.class, args);
        // test with: mvn clean spring-boot:run -Drun
        // .jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5005"
        // write to a log
        log.info("\n\n Application cloud.qasino.cards started: \n - environment [" + activeProfile +
                "] \n - command-line arguments [" + args.toString() + "]\n");

    }

    @Bean
    ApplicationRunner init(
            VisitorRepository visitorRepository,
            LeagueRepository leagueRepository,
            GameRepository gameRepository,
            PlayerRepository playerRepository,
            CardRepository cardRepository,
            TurnRepository turnRepository,
            CardMoveRepository cardMoveRepository
    ) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
//        Visitor visitor = new Visitor("visitorNameName", 1, "a@b.c");
//        int pawn = Visitor.pawnShipValue(0);
//        visitor.pawnShip(pawn);
//        visitor = visitorRepository.save(visitor);
//
//        League league = new League(visitor, "default", 1);
//        league.endLeagueThisMonth();
//        leagueRepository.save(league);
//
//        Game game = new Game(league, "highlow", visitor.getVisitorId()," " ,100);
//        game.shuffleGame(0);
//        game.setState(GameState.PLAYING);
//        game = gameRepository.save(game);
//        cardRepository.saveAll(game.getCards());
//
//        List<Player> players = new ArrayList<>();
//        players.add(playerRepository.save(new Player(visitor, game, Role.INITIATOR,
//                visitor.getBalance(), 1, Avatar.ELF, AiLevel.HUMAN)));
//        players.add(playerRepository.save(new Player(null, game, Role.BOT, visitor.getBalance(), 2,
//                Avatar.GOBLIN, AiLevel.AVERAGE)));
//        game.setPlayers(players);
//
//        Turn turn = new Turn( game, players.get(0).getPlayerId());
//        turnRepository.save(turn);
//        CardMove cardMove = new CardMove(turn, players.get(0), null, Move.DEAL,
//                Location.HAND);
//        cardMoveRepository.save(cardMove);
//
        return null;
    }

}
