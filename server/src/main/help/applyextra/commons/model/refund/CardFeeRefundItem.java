package applyextra.commons.model.refund;

import lombok.*;
import org.joda.time.DateTime;
import org.joda.time.Months;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardFeeRefundItem implements RefundItem{

    // TODO check if BigDecimal is ok
    private BigInteger yearlyCardFeeAmount;

    private Date anniversaryDate;

    private Date closeDate;

    public int getMonthsToRefund(){

        int months = Months.monthsBetween(new DateTime(closeDate), new DateTime(anniversaryDate)).getMonths();

        if(months == 0) {
            int closeDay = new DateTime(closeDate).getDayOfMonth();
            int anniversaryDay = new DateTime(anniversaryDate).getDayOfMonth();
            if(closeDay <= anniversaryDay) {
                months++;
            }
        }
        return months;
    }


    public BigDecimal getMonthlyFeeAmount(){
        return new BigDecimal(yearlyCardFeeAmount.divide(BigInteger.valueOf(12)));
    }

    @Override
    public BigDecimal getRefundAmount() {
        //TODO this calculation is not correct
        return (getMonthlyFeeAmount().multiply(BigDecimal.valueOf(getMonthsToRefund()))).divide(BigDecimal.valueOf(100));
    }
}
