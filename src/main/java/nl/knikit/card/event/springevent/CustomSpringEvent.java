package nl.knikit.card.event.springevent;

import org.springframework.context.ApplicationEvent;

/**
 * Events are one of the more overlooked functionalities in the framework but also one of the more
 * useful. And – like many other things in Spring – event publishing is one of the capabilities
 * provided by ApplicationContext.
 *
 * There are a few simple guidelines to follow:
 *
 * the event should extend ApplicationEvent
 * the publisher should inject an ApplicationEventPublisher object
 * the listener should implement the ApplicationListener interface
 *
 *
 * This class is just a placeholder to store the event data. In this case, the event class holds a
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
