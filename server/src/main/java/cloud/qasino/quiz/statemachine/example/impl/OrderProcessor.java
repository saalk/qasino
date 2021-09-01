package cloud.qasino.quiz.statemachine.example.impl;

import cloud.qasino.quiz.statemachine.example.ProcessData;
import cloud.qasino.quiz.statemachine.example.ProcessException;
import cloud.qasino.quiz.statemachine.example.Processor;
import cloud.qasino.quiz.statemachine.example.impl.OrderData;
import cloud.qasino.quiz.statemachine.example.impl.OrderEvent;
import org.springframework.stereotype.Service;

@Service
public class OrderProcessor implements Processor {
    @Override
    public ProcessData process(ProcessData data) throws ProcessException {
        ((OrderData)data).setEvent(OrderEvent.orderCreated);
        return data;
    }
}
