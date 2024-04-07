package cloud.qasino.games;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * A Servlet is a java class that handles requests, processes them and reply with a response eg.
 * - collect input from a user through an HTML form (xx extends HttpServlet + @WebServlet)
 * - query records from a database (xx extends GenericServlet )
 * - create web pages dynamically.
 *
 * Servlets are under the control of a Servlet Container (runtime environment). A Web Server calls
 * the Servlet/Web Container which in turn passes it to the target Servlet.
 * This concept is the driver for
 * - JSP (JavaServer Pages) - html pages renamed to .jsp with <% java code %> // web.xml lists them
 * - Spring MVC - part of Spring Boot
 *
 * Web Container examples are Tomcat, Jetty, and GlassFish, a Spring container works together with
 * them and focuses on Spring beans and their relations (eg db connect, request mapping).
 * Web Server examples are Apache HTTPS, Nginx and Microsoft IIS.
 */

/**
 * In the Spring container, beans declared in ApplicationContext.xml. Beans have 5 scopes:
 * common beans with ApplicationContext:
 * 1) Singleton (default scope) - you always get the same bean
 * 2) Prototype - you will get a new instance of the spring bean
 * for web aware applications with WebApplicationContext:
 * 3) Request - each http request has its own bean
 * 4) Session - each session has its own bean
 * 5) Global-session
 */

/**
 * Sping looks for beans in web.xml -> applicationContext.xml -> spring-servlet.xml
 * /WEB-INF/jsp/
 * web.xml -
 * /WEB-INF/applicationContext.xml - central in spring boot (@SpringBootApplication creates)
 * -> for properties, basic data source,
 * spring-servlet.xml - handles incoming requests and can scan for components
 * -> /WEB-INF/jsp/
 */

/*Java configuration typically uses @Bean-annotated methods within a @Configuration class. The @Bean annotation on a method indicates that the method creates a Spring bean. Moreover, a class annotated with @Configuration
indicates that it contains Spring bean configurations.
https://www.baeldung.com/spring-application-context
*/

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableJpaRepositories(basePackages = "cloud.qasino.games.*")
@EnableTransactionManagement
@EnableAutoConfiguration
@EntityScan(basePackages = "cloud.qasino.games.*")
@EnableJpaAuditing
@Slf4j
public class GamesApplication {

    public static void main(String[] args) {
        SpringApplication.run(GamesApplication.class, args);
    }
}
