package cloud.qasino.card.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * In spring framework bean declared in ApplicationContext.xml can reside in five scopes:
 *
 * common beans with ApplicationContext:
 * 1) Singleton (default scope) - you always get the same bean
 * 2) prototype - you will get a new instance of the spring bean
 *
 * only for web aware applications with WebApplicationContext:
 * 3) request - each http request has its own bean
 * 4) session - each session has its own bean
 * 5) global-session
 *
 */
@Slf4j
@SpringBootApplication
@EnableAutoConfiguration
@EnableResourceServer
@EnableJpaRepositories(basePackages="cloud.qasino.*")
@EnableTransactionManagement
@EntityScan(basePackages="cloud.qasino.*")
public class GamesApplication {

	@Value("${spring.profiles.active:dev}")
	private static String activeProfile; // dev = default

	public static void main(String[] args) {
		SpringApplication.run(GamesApplication.class, args);

		// write to a log
		log.info("\n\n Application cloud.qasino.cards started: \n - environment ["+ activeProfile +
				"] \n - command-line arguments [" + args.toString() +"]\n");
	}
}
