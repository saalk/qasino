package cloud.qasino.quiz.statemachine.example.impl;

import cloud.qasino.quiz.statemachine.example.ProcessEvent;
import cloud.qasino.quiz.statemachine.example.ProcessState;
import cloud.qasino.quiz.statemachine.example.Processor;
import cloud.qasino.quiz.statemachine.example.impl.OrderProcessor;
import cloud.qasino.quiz.statemachine.example.impl.OrderState;
import cloud.qasino.quiz.statemachine.example.impl.PaymentProcessor;

/**
 * DEFAULT    -  submit -> orderProcessor()   -> orderCreated   -> PMTPENDING
 * PMTPENDING -  pay    -> paymentProcessor() -> paymentError   -> PMTPENDING
 * PMTPENDING -  pay    -> paymentProcessor() -> paymentSuccess -> COMPLETED
 */
public enum OrderEvent implements ProcessEvent {
    submit {
        @Override
        public Class<? extends Processor> nextStepProcessor(ProcessEvent event) {
            return OrderProcessor.class;
        }
        /**
         * This move has no effect on state so return current state
         */
        @Override
        public ProcessState nextState(ProcessEvent event) {
            return OrderState.Default;
        }
    },
    orderCreated {
        /**
         * This move does not trigger any process
         * So return null
         */
        @Override
        public Class<? extends Processor> nextStepProcessor(ProcessEvent event) {
            return null;
        }
        @Override
        public ProcessState nextState(ProcessEvent event) {
            return OrderState.PaymentPending;
        }
    },
    pay {
        @Override
        public Class<? extends Processor> nextStepProcessor(ProcessEvent event) {
            return PaymentProcessor.class;
        }
        /**
         * This move has no effect on state so return current state
         */
        @Override
        public ProcessState nextState(ProcessEvent event) {
            return OrderState.PaymentPending;
        }
    },
    paymentSuccess {
        /**
         * This move does not trigger any process
         * So return null
         */
        @Override
        public Class<? extends Processor> nextStepProcessor(ProcessEvent event) {
            return null;
        }
        @Override
        public ProcessState nextState(ProcessEvent event) {
            return OrderState.Completed;
        }
    },
    paymentError {
        /**
         * This move does not trigger any process
         * So return null
         */
        @Override
        public Class<? extends Processor> nextStepProcessor(ProcessEvent event) {
            return null;
        }
        @Override
        public ProcessState nextState(ProcessEvent event) {
            return OrderState.PaymentPending;
        }
    };
}
