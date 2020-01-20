package cloud.qasino.card.statemachine.example.impl;

import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

//persists state of the data
//here we are using HashMap for illustration purposes
@Service
public class OrderDbService {
    private final ConcurrentHashMap<UUID, OrderState> states;
    public OrderDbService() {
        this.states = new ConcurrentHashMap<UUID, OrderState>();
    }
    public ConcurrentHashMap<UUID, OrderState> getStates() {
        return states;
    }
}
