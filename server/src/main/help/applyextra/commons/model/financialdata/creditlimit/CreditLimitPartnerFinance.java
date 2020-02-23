package applyextra.commons.model.financialdata.creditlimit;

import lombok.Getter;
import lombok.Setter;
import applyextra.commons.model.SourceOfIncome;
import applyextra.commons.model.financialdata.PartnerFinance;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
public class CreditLimitPartnerFinance implements PartnerFinance {

    @NotNull(message = "{partner.sourceOfIncome.missing}")
    private SourceOfIncome sourceOfIncome;

    @DecimalMin("0")
    @NotNull
    private BigDecimal monthlyNetIncome;

    private Boolean incomeFullLastYear;

    /////////////
    // EXPENSES
    /////////////

    @NotNull
    private Boolean childrenPresent;

    @DecimalMin("0")
    private BigDecimal monthlyAlimony;
}
