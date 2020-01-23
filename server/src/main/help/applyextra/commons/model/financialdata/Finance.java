package applyextra.commons.model.financialdata;

import applyextra.commons.model.Loan;
import applyextra.commons.model.SourceOfIncome;

import java.math.BigDecimal;
import java.util.List;

public interface Finance {

    SourceOfIncome getSourceOfIncome();

    BigDecimal getMonthlyNetIncome();

    Boolean getIncomeFullLastYear();

    BigDecimal getMonthlyAlimony();

    HousingCostsType getHousingCostsType();

    BigDecimal getMonthlyHousingCosts();

    Boolean getChildrenPresent();

    MaritalStatus getMaritalStatus();

    String getStartContractDate();

    String getEndContractDate();

    String getTimeSinceFreelancer();

    Boolean getCostPaidWithING();

    List<Loan> getExtraLoans();
}
