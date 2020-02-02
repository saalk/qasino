package applyextra.commons.event;

/**
 * This interface allows events to indicate to FlowDTO object, when they are starting and ending. This can be used if different
 * events share the same data types, which have different values.
 *
 * FlowDTO's should not alter the move
 */
public interface FlowEventCallback {

    /**
     * Called by the move when it is starting to execute
     *
     * @param event
     */
    void startingEvent(AbstractEvent event);

    /**
     * Called by the move when it is finished executing
     *
     * @param event
     */
    void endingEvent(AbstractEvent event);

}
