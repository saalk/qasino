package cloud.qasino.card.statemachine.example.impl;

import cloud.qasino.card.statemachine.example.ProcessData;
import cloud.qasino.card.statemachine.example.ProcessException;
import cloud.qasino.card.statemachine.example.Processor;
import org.springframework.stereotype.Service;

@Service
public class PaymentProcessor implements Processor {
    @Override
    public ProcessData process(ProcessData data) throws ProcessException {
        if(((OrderData)data).getPayment() < 1.00) {
            ((OrderData)data).setEvent(OrderEvent.paymentError);
            throw new PaymentException(OrderEvent.paymentError.name());
        } else {
            ((OrderData)data).setEvent(OrderEvent.paymentSuccess);
        }
        return data;
    }
}
