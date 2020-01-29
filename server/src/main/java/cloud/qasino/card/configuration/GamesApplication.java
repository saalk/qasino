package cloud.qasino.card.configuration;

import cloud.qasino.card.entity.Game;
import cloud.qasino.card.entity.League;
import cloud.qasino.card.entity.Player;
import cloud.qasino.card.entity.User;
import cloud.qasino.card.entity.enums.player.AiLevel;
import cloud.qasino.card.entity.enums.player.Avatar;
import cloud.qasino.card.entity.enums.player.Role;
import cloud.qasino.card.repositories.*;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@SpringBootApplication
@EnableAutoConfiguration
//@EnableResourceServer //
@EnableJpaRepositories(basePackages = "cloud.qasino.*")
@EnableTransactionManagement
@EntityScan(basePackages = "cloud.qasino.*")
@EnableJpaAuditing
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
            UserRepository userRepository,
            LeagueRepository leagueRepository,
            GameRepository gameRepository,
            PlayerRepository playerRepository,
            PlayingCardRepository playingCardRepository
    ) {
        User user = new User("aliasName", 1, "a@b.c");
        int pawn = User.pawnShipValue(0);
        user.pawnShip(pawn);
        user = userRepository.save(user);

        League league = new League(user, "leagueName", 1);
        league.endLeagueThisMonth();
        leagueRepository.save(league);

        Game game = new Game(league, "highlow", "", 100);
        game.shuffleGame(0);
        game = gameRepository.save(game);
        playingCardRepository.saveAll(game.getPlayingCards());

        List<Player> players = new ArrayList<>();
        players.add(playerRepository.save(new Player(user, game, Role.INITIATOR,
                user.getBalance(), 1, Avatar.ELF, AiLevel.HUMAN)));
        players.add(playerRepository.save(new Player(null, game, Role.BOT, user.getBalance(), 2,
                Avatar.GOBLIN, AiLevel.AVERAGE)));
        game.setPlayers(players);

        return null;
    }

}
