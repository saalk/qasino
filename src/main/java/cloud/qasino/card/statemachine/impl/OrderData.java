package cloud.qasino.card.statemachine.impl;

import lombok.*;
import cloud.qasino.card.statemachine.ProcessData;
import cloud.qasino.card.statemachine.ProcessEvent;

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
