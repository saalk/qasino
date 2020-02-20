package cloud.qasino.card.statemachine.example.impl;

import cloud.qasino.card.statemachine.example.ProcessData;
import cloud.qasino.card.statemachine.example.ProcessException;
import cloud.qasino.card.statemachine.example.Processor;
import org.springframework.stereotype.Service;

@Service
public class OrderProcessor implements Processor {
    @Override
    public ProcessData process(ProcessData data) throws ProcessException {
        ((OrderData)data).setEvent(OrderEvent.orderCreated);
        return data;
    }
}
