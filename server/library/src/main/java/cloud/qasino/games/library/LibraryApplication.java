package cloud.qasino.games.library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * this module which consists of rest services can also run without web module.
 */
@Configuration
@ComponentScan("cloud.qasino.games.library.*")
//@SpringBootApplication
//@EnableJpaRepositories
public class LibraryApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(LibraryApplication.class, args);
    }
}