package cloud.qasino.quiz.action.interfaces;

public abstract class AbstractValidation {

	 // Will validate whether or not the action is able to run, if not an exception
	// should be thrown

	public abstract void validate(Object ...o);
}
