package applyextra.commons.model.financialdata.creditlimit;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;
import applyextra.commons.model.Loan;
import applyextra.commons.model.SourceOfIncome;
import applyextra.commons.model.financialdata.Finance;
import applyextra.commons.model.financialdata.HousingCostsType;
import applyextra.commons.model.financialdata.MaritalStatus;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreditLimitFinance implements Finance {

    @NotNull(message = "{main.sourceOfIncome.missing}")
    @JsonDeserialize(using = SourceofIncomeEnumDeserializer.class)
    private SourceOfIncome sourceOfIncome;

    @DecimalMin("650")
    @NotNull
    private BigDecimal monthlyNetIncome;

    private Boolean incomeFullLastYear;

    /////////////
    // EXPENSES
    /////////////

    private HousingCostsType housingCostsType;

    @DecimalMin("0")
    private BigDecimal monthlyHousingCosts;

    private Boolean childrenPresent;

    @DecimalMin("0")
    private BigDecimal monthlyAlimony;

    private String startContractDate;

    private String endContractDate;

    private String timeSinceFreelancer;

    private Boolean costPaidWithING;

    /////////////
    // OTHER
    /////////////

    private MaritalStatus maritalStatus;

    @Valid
    private List<Loan> extraLoans;
}
