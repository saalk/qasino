package cloud.qasino.card.dto.event;

public abstract class AbstractValidation {
	/**
	 * Will validate whether or not the event/activity is able to run, if not an exception should be thrown
	 */
	public abstract void validate(Object ...o);
}
