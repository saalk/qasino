package cloud.qasino.quiz.statemachine.example.impl;

import cloud.qasino.quiz.statemachine.example.impl.OrderState;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

//persists state of the data
//here we are using HashMap for illustration purposes
@Service
public class OrderDbService {
    private final ConcurrentHashMap<UUID, cloud.qasino.quiz.statemachine.example.impl.OrderState> states;
    public OrderDbService() {
        this.states = new ConcurrentHashMap<UUID, cloud.qasino.quiz.statemachine.example.impl.OrderState>();
    }
    public ConcurrentHashMap<UUID, OrderState> getStates() {
        return states;
    }
}
