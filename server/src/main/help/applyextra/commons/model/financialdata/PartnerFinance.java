package applyextra.commons.model.financialdata;


import applyextra.commons.model.SourceOfIncome;

import java.math.BigDecimal;

public interface PartnerFinance {

    SourceOfIncome getSourceOfIncome();

    BigDecimal getMonthlyNetIncome();

    Boolean getIncomeFullLastYear();

    BigDecimal getMonthlyAlimony();

    Boolean getChildrenPresent();

}
