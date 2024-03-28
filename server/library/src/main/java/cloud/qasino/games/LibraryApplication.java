package cloud.qasino.games;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * this module which consists of rest services can also run without web module.
 */
@Configuration
@ComponentScan("cloud.qasino.games.*")
@SpringBootApplication
@EnableJpaRepositories
public class LibraryApplication extends SpringBootServletInitializer {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(LibraryApplication.class, args);
    }

    @Override
    public void onStartup(jakarta.servlet.ServletContext servletContext) {
        servletContext.setInitParameter("com.sun.faces.expressionFactory", "org.apache.el.ExpressionFactoryImpl");

    }
}