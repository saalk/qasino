package cloud.qasino.games.statemachine.example.impl;

import cloud.qasino.games.statemachine.example.ProcessState;

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
