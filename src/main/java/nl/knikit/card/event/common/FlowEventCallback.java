package nl.knikit.card.event.common;

/**
 * This interface allows events to indicate to FlowDTO object, when they are starting and ending. This can be used if different
 * events share the same data types, which have different values.
 *
 * FlowDTO's should not alter the event
 */
public interface FlowEventCallback {

    /**
     * Called by the event when it is starting to execute
     *
     * @param event
     */
    void startingEvent(AbstractEvent event);

    /**
     * Called by the event when it is finished executing
     *
     * @param event
     */
    void endingEvent(AbstractEvent event);

}
