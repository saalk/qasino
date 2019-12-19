package nl.knikit.card.statemachine.impl;

import nl.knikit.card.statemachine.ProcessData;
import nl.knikit.card.statemachine.ProcessException;
import nl.knikit.card.statemachine.Processor;
import org.springframework.stereotype.Service;

@Service
public class OrderProcessor implements Processor {
    @Override
    public ProcessData process(ProcessData data) throws ProcessException {
        ((OrderData)data).setEvent(OrderEvent.orderCreated);
        return data;
    }
}
