package applyextra.commons.state;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@Scope("prototype")
public class CreditCardsStateMachine {

    /**
     * Please take note, if you create a new State, the new commons version has to be included in the Datalake solution before we put this state live
     * If that is not done, exceptions will occur, and the datalake transfer will fail.
     */
    public enum State {
        LISTED,
        STARTED,
        INITIATED,
        VERIFIED,
        CHECKED,
        SCORING_FAILED,
        SCORE_CHECKED,
        LIMIT_CHECKED,
        SUBMITTED,
        AUTHORIZED,
        REVERIFIED,
        RECHECKED,
        EXECUTED,
        FULFILLED_CUSTOMER,
        FULFILLED,
        DISTRIBUTED,
        CANCELLED,
        ERROR,
        EXPIRED,
        FAILED,
        DECLINED,
        TIMEOUT,
        PRE_EXECUTED,
        POST_RECHECKED,
        REGISTRATIONS_HANDLED,
        PEGA_AWAITED,
        EXCEPTION_LIMIT_CHECKED,
        INBOX_MSG_OFF_BEFORE_EXECUTED,
        INBOX_MSG_OFF_AFTER_EXECUTED
    }

    @Deprecated
    public enum Trigger {
        OK,
        PASS,
        NOT_OK,
        EXPIRE,
        CANCEL,
        ERROR,
        DECLINE,
        RESET,
        TIMEOUT
    }
}
