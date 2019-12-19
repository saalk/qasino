package nl.knikit.card.statemachine.impl;

import nl.knikit.card.statemachine.ProcessData;
import nl.knikit.card.statemachine.ProcessException;
import nl.knikit.card.statemachine.Processor;
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
