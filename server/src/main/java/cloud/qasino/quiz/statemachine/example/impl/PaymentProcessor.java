package cloud.qasino.quiz.statemachine.example.impl;

import cloud.qasino.quiz.statemachine.example.ProcessData;
import cloud.qasino.quiz.statemachine.example.ProcessException;
import cloud.qasino.quiz.statemachine.example.Processor;
import cloud.qasino.quiz.statemachine.example.impl.OrderData;
import cloud.qasino.quiz.statemachine.example.impl.OrderEvent;
import cloud.qasino.quiz.statemachine.example.impl.PaymentException;
import org.springframework.stereotype.Service;

@Service
public class PaymentProcessor implements Processor {
    @Override
    public ProcessData process(ProcessData data) throws ProcessException {
        if(((cloud.qasino.quiz.statemachine.example.impl.OrderData)data).getPayment() < 1.00) {
            ((cloud.qasino.quiz.statemachine.example.impl.OrderData)data).setEvent(cloud.qasino.quiz.statemachine.example.impl.OrderEvent.paymentError);
            throw new PaymentException(cloud.qasino.quiz.statemachine.example.impl.OrderEvent.paymentError.name());
        } else {
            ((OrderData)data).setEvent(OrderEvent.paymentSuccess);
        }
        return data;
    }
}
