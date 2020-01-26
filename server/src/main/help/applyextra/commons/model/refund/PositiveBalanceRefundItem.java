package applyextra.commons.model.refund;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PositiveBalanceRefundItem implements RefundItem {

    private BigDecimal availableCreditAmount;

    private BigDecimal creditLimit;

    @Override
    public BigDecimal getRefundAmount(){
        return availableCreditAmount.subtract(creditLimit);
    }

}
