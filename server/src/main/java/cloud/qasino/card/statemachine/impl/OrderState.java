package cloud.qasino.card.statemachine.impl;

import cloud.qasino.card.statemachine.ProcessState;

/**
 * DEFAULT    -  submit -> orderProcessor()   -> orderCreated   -> PMTPENDING
 * PMTPENDING -  pay    -> paymentProcessor() -> paymentError   -> PMTPENDING
 * PMTPENDING -  pay    -> paymentProcessor() -> paymentSuccess -> COMPLETED
 */
public enum OrderState implements ProcessState {
    Default,
    PaymentPending,
    Completed;
}
