package cloud.qasino.quiz.event.interfaces;

import cloud.qasino.quiz.event.interfaces.AbstractEvent;

/**
 * This interface allows events to indicate to FlowDTO object, when they are starting and ending.
 * This can be used if different events share the same data types, which have different values.
 * FlowDTO's should not alter the action
 */
public interface FlowEventCallback {

    /**
     * Called by the action when it is starting to execute
     */
    void startingEvent(cloud.qasino.quiz.event.interfaces.AbstractEvent action);

    /**
     * Called by the action when it is finished executing
     */
    void endingEvent(AbstractEvent action);

}
