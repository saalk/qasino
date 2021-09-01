package cloud.qasino.quiz.event;

import org.springframework.context.ApplicationEvent;

/**
 * Events are one of the more overlooked functionalities in the framework but also one of the more
 * useful. And – like many other things in Spring – move publishing is one of the capabilities
 * provided by ApplicationContext.
 *
 * There are a few simple guidelines to follow:
 *
 * the move should extend ApplicationEvent
 * the publisher should inject an ApplicationEventPublisher object
 * the listener should implement the ApplicationListener interface
 *
 *
 * This class is just a placeholder to store the move data. In this case, the move class holds a
 * String message
 *
 */
public class CustomSpringEvent extends ApplicationEvent {
    private String message;

    public CustomSpringEvent(Object source, String message) {
        super(source);
        this.message = message;
    }
    public String getMessage() {
        return message;
    }
}
