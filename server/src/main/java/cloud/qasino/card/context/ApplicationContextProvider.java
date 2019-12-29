package cloud.qasino.card.context;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;


/**
 * this class can be deleted if you just use field based injection to get a bean
 *
 * @Autowired private ApplicationContext applicationContext;
 * <p>
 * However Field based injection ApplicationContext (@Autowired and Field injection) should be
 * avoided due to issues that may arise.
 * <p>
 * This means that if you want to use your class outside the context container, for entity for
 * unit testing, you are forced to use a Spring container to instantiate your class as there is no
 * other possible way (but reflection) to set the autowired fields.
 * <p>
 * The recommended approach is then to use constructor-based and setter-based dependency injection.
 * Constructor-based injection is recommended for required dependencies allowing them to be
 * immutable and preventing them to be null. Setter-based injection is recommended for
 * optional dependencies.
 */

@Component
/**
 * 1: Get ApplicationContext and a bean in SpringBoot with this class!
 *
 * If you autowire this class and you can get any bean with the following command
 * <AnnotatedClass> AnnotatedClass =
 * applicationContextProvider.getApplicationContext().getBean(<AnnotatedClass>.class);
 */
public class ApplicationContextProvider implements ApplicationContextAware {

    /**
     * 1: use a getBean() method
     * <p>
     * The ApplicationContext is the central interface within a Spring context for providing
     * configuration information to the context. The ApplicationContext interface provides
     * the getBean() method to retrieve bean from the spring container.
     * <p>
     * NB
     * ApplicationContext instantiates Singleton bean when the container is started,
     * It doesn't wait for getBean to be called as opposed to using BeanFactory
     */
    ApplicationContext applicationContext;

    public ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }

    @Override
    /**
     * The ApplicationContextAware interface has this method that can be implemented by any
     * object that wishes to be notified of the ApplicationContext that it runs in.
     */
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}