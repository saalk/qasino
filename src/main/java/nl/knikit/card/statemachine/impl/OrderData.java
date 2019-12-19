package nl.knikit.card.statemachine.impl;

import lombok.*;
import nl.knikit.card.statemachine.ProcessData;
import nl.knikit.card.statemachine.ProcessEvent;

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
