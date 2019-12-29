package cloud.qasino.card.statemachine.impl;

import cloud.qasino.card.statemachine.ProcessData;
import cloud.qasino.card.statemachine.ProcessException;
import cloud.qasino.card.statemachine.Processor;
import org.springframework.stereotype.Service;

@Service
public class OrderProcessor implements Processor {
    @Override
    public ProcessData process(ProcessData data) throws ProcessException {
        ((OrderData)data).setEvent(OrderEvent.orderCreated);
        return data;
    }
}
