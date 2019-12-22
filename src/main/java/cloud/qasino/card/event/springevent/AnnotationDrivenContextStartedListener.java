package cloud.qasino.card.event.springevent;

import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class AnnotationDrivenContextStartedListener {

    /**
     * But now making it asynchronous is as simple as adding an @Async annotation (do not forget
     * to enable Async support in the context)
     *
     */
    //@Async
    @EventListener
    public void handleContextStart(ContextStartedEvent cse) {
        System.out.println("Handling context started event.");
    }
}