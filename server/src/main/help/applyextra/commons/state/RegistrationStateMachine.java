package applyextra.commons.state;

public interface RegistrationStateMachine {

    enum State {
        REQUEST_RECEIVED,
        BKR_PROCESS_INITIATED,
        BKR_FULFILLED,
        INVALID_REQUEST,
        CHANGE_CARD,
        ERROR,
        TIMEOUT,
        FULFILLED
    }

}
