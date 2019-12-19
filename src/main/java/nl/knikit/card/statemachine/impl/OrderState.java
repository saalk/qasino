package nl.knikit.card.statemachine.impl;

import nl.knikit.card.statemachine.ProcessState;

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
