package cloud.qasino.card.configuration;

import cloud.qasino.card.entity.User;
import cloud.qasino.card.repositories.PlayerRepository;
import cloud.qasino.card.repositories.UserRepository;
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

import java.util.stream.Stream;

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
//@EnableResourceServer
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
            PlayerRepository playerRepository
    ) {
        return args -> {
            Stream.of("Alias", "Alias", "Alias", "Alias").forEach(alias -> {
                long count = userRepository.countByAlias(alias);
                User user = new User();
                user.setAlias(alias);
                user.setAliasSequence((int) count);
                userRepository.save(user);
            });
            //userRepository.findAll().forEach(System.out::println);

        };
    }

 /*   @Bean
    public FilterRegistrationBean simpleCorsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Collections.singletonList("*"));
        config.setAllowedMethods(Collections.singletonList("*"));
        config.setAllowedHeaders(Collections.singletonList("*"));
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }*/

}
