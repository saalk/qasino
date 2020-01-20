package cloud.qasino.card.statemachine.example.impl;

import lombok.*;
import cloud.qasino.card.statemachine.example.ProcessData;
import cloud.qasino.card.statemachine.example.ProcessEvent;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class OrderData implements ProcessData {
    private double payment;
    private ProcessEvent event;
    private UUID orderId;
    @Override
    public ProcessEvent getEvent() {
        return this.event;
    }
}
