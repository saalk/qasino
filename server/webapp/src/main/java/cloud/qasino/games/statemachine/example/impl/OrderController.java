package cloud.qasino.games.statemachine.example.impl;

import lombok.RequiredArgsConstructor;
import cloud.qasino.games.statemachine.example.ProcessException;
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
        OrderData data = new OrderData();
        data.setPayment(payment);
        data.setOrderId(orderId);
        data.setEvent(OrderEvent.pay);
        data = (OrderData)stateTrasitionsManager.processEvent(data);
        return ((OrderEvent)data.getEvent()).name();
    }
    @ExceptionHandler(value=OrderException.class)
    public String handleOrderException(OrderException e) {
        return e.getMessage();
    }
    @GetMapping("/order")
    public String handleOrderSubmit() throws ProcessException {
        OrderData data = new OrderData();
        data.setEvent(OrderEvent.submit);
        data = (OrderData)stateTrasitionsManager.processEvent(data);
        return ((OrderEvent)data.getEvent()).name() + ", orderId = " + data.getOrderId();
    }
}