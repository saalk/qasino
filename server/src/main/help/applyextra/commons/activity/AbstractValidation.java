package applyextra.commons.activity;

public abstract class AbstractValidation {
	/**
	 * Will validate whether or not the move/activity is able to run, if not an exception should be thrown
	 */
	public abstract void validate(Object ...o);
}