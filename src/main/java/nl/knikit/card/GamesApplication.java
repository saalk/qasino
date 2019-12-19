package nl.knikit.card;

import lombok.extern.slf4j.Slf4j;
import nl.knikit.card.context.ApplicationContextProvider;
import nl.knikit.card.util.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

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
/**
 * @SpringBootApplication defines an automatic component scan on sup and super packages
 * but not on different 'branched of super' packages
 * It also indicates that this class has @Bean definition methods to instantiate and configure
 * the dependencies
 *
 * 1: add the right annotation
 * @Component is a generic stereotype for any Spring-managed component or bean.
 * - @Repository is a stereotype for the persistence layer.
 * - @Service is a stereotype for the service layer.
 * - @Controller is a stereotype for the presentation layer (spring-MVC).
 *
 * 2: These annotations indicate that a class might be a candidate for creating a bean.
 * Its like putting a hand up.
 *
 * 3: @ComponentScan is searching packages for Components. Trying to find out who all put their
 * hands up. These will be maintained by SpringApplicationContext. DispatcherServlet will look for
 * @RequestMapping on classes which are annotated using @Controller but not with @Component.
 *
 */
public class GamesApplication implements ApplicationRunner {

	public static void main(String[] args) {
		SpringApplication.run(GamesApplication.class, args);
	}

	@Autowired
	ApplicationContextProvider applicationContextProvider;

	@Override
	public void run(ApplicationArguments args) throws Exception {

		// write to a log
		log.info("Application nl.knikit.cards started with command-line arguments: {}",
				args.getOptionNames());

		// show a message
		Message message = applicationContextProvider.getApplicationContext().getBean(Message.class);
		System.out.println(message.getMessage());

		// start batch jobs or source data from other microservices via service discovery
	}

	/**
	 * In some cases, publishing events synchronously isn't really what we're looking for –
	 * we may need async handling of our events.
	 *
	 * You can turn that on in the configuration by creating an ApplicationEventMulticaster
	 * bean with an executor; for our purposes here SimpleAsyncTaskExecutor works well:
	 *
	 */
	@Bean(name = "applicationEventMulticaster")
	public ApplicationEventMulticaster simpleApplicationEventMulticaster() {
		SimpleApplicationEventMulticaster eventMulticaster =
				new SimpleApplicationEventMulticaster();

		eventMulticaster.setTaskExecutor(new SimpleAsyncTaskExecutor());
		return eventMulticaster;
	}

}
