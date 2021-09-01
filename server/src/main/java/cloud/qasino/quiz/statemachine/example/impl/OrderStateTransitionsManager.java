package cloud.qasino.quiz.statemachine.example.impl;

import cloud.qasino.quiz.statemachine.example.impl.OrderData;
import cloud.qasino.quiz.statemachine.example.impl.OrderDbService;
import cloud.qasino.quiz.statemachine.example.impl.OrderEvent;
import cloud.qasino.quiz.statemachine.example.impl.OrderException;
import cloud.qasino.quiz.statemachine.example.impl.OrderState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import cloud.qasino.quiz.statemachine.example.AbstractStateTransitionsManager;
import cloud.qasino.quiz.statemachine.example.ProcessData;
import cloud.qasino.quiz.statemachine.example.ProcessException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class manages various state transitions
 * based on the move
 * The superclass AbstractStateTransitionsManager
 * calls the two methods initializeState and
 * processStateTransition in that order
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class OrderStateTransitionsManager extends AbstractStateTransitionsManager {
    private final ApplicationContext context;
    private final OrderDbService dbService;
    @Override
    protected ProcessData processStateTransition(ProcessData sdata) throws ProcessException {
        cloud.qasino.quiz.statemachine.example.impl.OrderData data = (cloud.qasino.quiz.statemachine.example.impl.OrderData) sdata;
        try {
            log.info("Pre-move: " + data.getEvent().toString());
            data = (cloud.qasino.quiz.statemachine.example.impl.OrderData) this.context.getBean(data.getEvent().nextStepProcessor(data.getEvent())).process(data);
            log.info("Post-move: " + data.getEvent().toString());
            dbService.getStates().put(data.getOrderId(), (cloud.qasino.quiz.statemachine.example.impl.OrderState)data.getEvent().nextState(data.getEvent()));
            log.info("Final state: " + dbService.getStates().get(data.getOrderId()).name());
            log.info("??*************************************");
        } catch (cloud.qasino.quiz.statemachine.example.impl.OrderException e) {
            log.info("Post-move: " + ((cloud.qasino.quiz.statemachine.example.impl.OrderEvent) data.getEvent()).name());
            dbService.getStates().put(data.getOrderId(), (cloud.qasino.quiz.statemachine.example.impl.OrderState)data.getEvent().nextState(data.getEvent()));
            log.info("Final state: " + dbService.getStates().get(data.getOrderId()).name());
            log.info("??*************************************");
            throw new cloud.qasino.quiz.statemachine.example.impl.OrderException(((OrderEvent) data.getEvent()).name(), e);
        }
        return data;
    }
    private cloud.qasino.quiz.statemachine.example.impl.OrderData checkStateForReturningCustomers(cloud.qasino.quiz.statemachine.example.impl.OrderData data) throws cloud.qasino.quiz.statemachine.example.impl.OrderException {
        // returning customers must have a state
        if (data.getOrderId() != null) {
            if (this.dbService.getStates().get(data.getOrderId()) == null) {
                throw new cloud.qasino.quiz.statemachine.example.impl.OrderException("No state exists for orderId=" + data.getOrderId());
            } else if (this.dbService.getStates().get(data.getOrderId()) == cloud.qasino.quiz.statemachine.example.impl.OrderState.Completed) {
                throw new cloud.qasino.quiz.statemachine.example.impl.OrderException("Order is completed for orderId=" + data.getOrderId());
            } else {
                log.info("Initial state: " + dbService.getStates().get(data.getOrderId()).name());
            }
        }
        return data;
    }
    @Override
    protected ProcessData initializeState(ProcessData sdata) throws OrderException {
        cloud.qasino.quiz.statemachine.example.impl.OrderData data = (OrderData) sdata;
        if (data.getOrderId() != null) {
            return checkStateForReturningCustomers(data);
        }
        UUID orderId = UUID.randomUUID();
        data.setOrderId(orderId);
        dbService.getStates().put(orderId, (cloud.qasino.quiz.statemachine.example.impl.OrderState) cloud.qasino.quiz.statemachine.example.impl.OrderState.Default);
        log.info("Initial state: " + dbService.getStates().get(data.getOrderId()).name());
        return data;
    }
    public ConcurrentHashMap<UUID, OrderState> getStates() {
        return dbService.getStates();
    }
}
