package cloud.qasino.quiz.statemachine.example.impl;

import cloud.qasino.quiz.statemachine.example.impl.OrderData;
import cloud.qasino.quiz.statemachine.example.impl.OrderEvent;
import cloud.qasino.quiz.statemachine.example.impl.OrderException;
import cloud.qasino.quiz.statemachine.example.impl.OrderStateTransitionsManager;
import lombok.RequiredArgsConstructor;
import cloud.qasino.quiz.statemachine.example.ProcessException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
public class OrderController {
    private final OrderStateTransitionsManager stateTrasitionsManager;
    @GetMapping("/order/cart")
    public String handleOrderPayment(
            @RequestParam double payment,
            @RequestParam UUID orderId) throws Exception {
        cloud.qasino.quiz.statemachine.example.impl.OrderData data = new cloud.qasino.quiz.statemachine.example.impl.OrderData();
        data.setPayment(payment);
        data.setOrderId(orderId);
        data.setEvent(cloud.qasino.quiz.statemachine.example.impl.OrderEvent.pay);
        data = (cloud.qasino.quiz.statemachine.example.impl.OrderData)stateTrasitionsManager.processEvent(data);
        return ((cloud.qasino.quiz.statemachine.example.impl.OrderEvent)data.getEvent()).name();
    }
    @ExceptionHandler(value= cloud.qasino.quiz.statemachine.example.impl.OrderException.class)
    public String handleOrderException(OrderException e) {
        return e.getMessage();
    }
    @GetMapping("/order")
    public String handleOrderSubmit() throws ProcessException {
        cloud.qasino.quiz.statemachine.example.impl.OrderData data = new cloud.qasino.quiz.statemachine.example.impl.OrderData();
        data.setEvent(cloud.qasino.quiz.statemachine.example.impl.OrderEvent.submit);
        data = (OrderData)stateTrasitionsManager.processEvent(data);
        return ((OrderEvent)data.getEvent()).name() + ", orderId = " + data.getOrderId();
    }
}
