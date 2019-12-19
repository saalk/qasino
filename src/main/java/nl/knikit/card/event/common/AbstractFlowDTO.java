package nl.knikit.card.event.common;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import nl.knikit.card.entity.Game;
import nl.knikit.card.statemachine.CardGameStateMachine;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
public abstract class AbstractFlowDTO implements FlowEventCallback {

    private Queue<AbstractEvent> chain;
    private boolean running = false;
    
    @Setter
    @Getter
    private CardGameStateMachine stateMachine;
    
    @Getter
    @Setter
    private Game gameContext;
    
    public AbstractFlowDTO addContext(Game context){
        this.gameContext = context;
	    String message = String.format("AbstractFlowDTO in addContext context is: %s", context);
	    log.info(message);
        return this;
    }
        
    public AbstractEvent getNextInFlow() {
        String message = String.format("AbstractFlowDTO in getNextInFlow chain is: %s", chain);
        log.info(message);
        return chain.poll();
    }

    public AbstractFlowDTO() {
        this(new ConcurrentLinkedQueue<AbstractEvent>());
    }

    public AbstractFlowDTO(ConcurrentLinkedQueue<AbstractEvent> queue) {
    	
        String message = String.format("AbstractFlowDTO in constructor with queue supplied is: %s", queue);
        log.info(message);
        
        this.chain = queue;
    }

    public boolean addEvent(AbstractEvent event) {
    
        if (!running) {
            chain.add(event);
            String message = String.format("AbstractFlowDTO in addEvent chain is: %s", chain);
            log.info(message);
        } else {
            String message = String.format("AbstractFlowDTO in addEvent is running so no addEvent to chain");
            log.info(message);
            return false;
        }
        return true;
    }

    public void start() {
        running = true;
        final AbstractEvent first = this.getNextInFlow();
        if (first == null) {
        	
            String message = String.format("AbstractFlowDTO in start no first in flow");
            log.info(message);
            
            return;
        }
        first.fireChainedEvent(this);
        String message = String.format("AbstractFlowDTO in start first is: %s", first);
        log.info(message);
    }

    public void transition(final CardGameStateMachine.Trigger trigger) {
        this.stateMachine.transition(trigger);
    }

    @Override
    public void startingEvent(final AbstractEvent event) {
        // stubbed for subclasses. Override if necessary
    }

    @Override
    public void endingEvent(final AbstractEvent event) {
        // stubbed for subclasses. Override if necessary
    }
}
