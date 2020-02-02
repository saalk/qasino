package cloud.qasino.card.event;

import cloud.qasino.card.event.interfaces.Event;

public enum EventEnum implements Event {

    // from the resource
    INITIALIZE,
    MARK_AS_EXCEPTION_PROCESS,
    LIST,
    CHECK,
    SELECT_LIMIT,
    CONFIRM_CHECK_PASSED,
    SUBMIT,
    AUTHORIZE,
    EXECUTE,
    ENTER_STATE,
    PERFORM_IN_PARALLEL,
    VERIFY,
    DISTRIBUTE,
    SCORE_CHECK,
    CAPACITY_CHECK,
    SEND_MESSAGE,
    RETRY,
    BKR_CHECK,
    RESET,
    CHECK_FOR_PEGACASE,
    CREATE_PEGACASE,
    REJECT,
    CANCEL,
    CREATE_CARD,
    EXCEPTION_VERIFY,
    EXCEPTION_APPROVE,
    RESET_APPROVE,
    EXCEPTION_CHECK,
    EXCEPTION_CAPACITY_CHECK

    // tech events
}
