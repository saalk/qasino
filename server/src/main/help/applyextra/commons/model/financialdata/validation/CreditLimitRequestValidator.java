package applyextra.commons.model.financialdata.validation;

import applyextra.commons.model.SourceOfIncome;
import applyextra.commons.model.financialdata.creditlimit.CreditLimitRequest;
import nl.ing.sc.creditcardmanagement.commonobjects.PortfolioCode;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.AssertTrue;

/**
 * This validator checks data integrity of so-called FinanceAndPartnerRequests. A FinanceRequest is an interface for
 * requests that contain financial data and partner data. Example is the credit limit request.
 */
public class CreditLimitRequestValidator {

    private CreditLimitRequest creditLimitRequest;

    public CreditLimitRequestValidator(CreditLimitRequest request) {
        this.creditLimitRequest = request;
    }

    ///////////////////////////////////////////////////////
    // SOURCE OF INCOME
    ///////////////////////////////////////////////////////

    /**
     * In case of a Revolving playingcard, finance is expected.
     * 
     * @return boolean
     */
    @AssertTrue(message = "{revolving.finances.missing}")
    public boolean isFinanceGivenForRevolving() {
        boolean valid = true;

        if (isRevolving()) {
            if (creditLimitRequest.getFinance() == null) {
                valid = false;
            }
        }
        return valid;
    }

    /**
     * In case of a temporary source of income it should be indicated by the requestor that the given monthly net income
     * has been received during the past 12 months (both in case of a Charge and Revolving playingcard). Note that when the
     * incomeFullLastYear is false, then REJECT
     * 
     * @return boolean
     */
    @AssertTrue(message = "{main.fullIncome.missingInvalid}")
    public boolean isIncomeFullLastYearGivenAndTrue() {
        boolean valid = true;

        if (creditLimitRequest.getFinance() != null && (SourceOfIncome.CONTRACT_BEPAALDE_TIJD == creditLimitRequest.getFinance()
                .getSourceOfIncome()
                || SourceOfIncome.UITZENDKRACHT == creditLimitRequest.getFinance()
                        .getSourceOfIncome())) {
            if (creditLimitRequest.getFinance()
                    .getIncomeFullLastYear() == null
                    || creditLimitRequest.getFinance()
                            .getIncomeFullLastYear() == false) {
                valid = false;
            }
        }
        return valid;
    }

    /**
     * In case the request is jobless and receives payment of social welfare, an unemployment benefit or none benefit,
     * then REJECT
     * 
     * @return boolean
     */
    @AssertTrue(message = "{revolving.benefit.reject}")
    public boolean isBenefitTypeAptForCreditCard() {
        boolean valid = true;

        if (creditLimitRequest.getFinance() != null
                && (SourceOfIncome.BIJSTAND_ABW_WWB == SourceOfIncome.fromCode(creditLimitRequest.getFinance()
                        .getSourceOfIncome()
                        .getCode())
                        || SourceOfIncome.WERKLOOSHEID_WW_IOAW_IOAZ == SourceOfIncome.fromCode(creditLimitRequest.getFinance()
                                .getSourceOfIncome()
                                .getCode())
                        || SourceOfIncome.GEEN_HUISVROUW_MAN == SourceOfIncome.fromCode(creditLimitRequest.getFinance()
                                .getSourceOfIncome()
                                .getCode()))) {
            valid = false;
        }
        return valid;
    }

    ///////////////////////////////////////////////////////
    // EXPENSES
    ///////////////////////////////////////////////////////

    /**
     * In case of a Revolving playingcard, some expense fields are required: housing cost type, monthly housing cost and the
     * children present indicator.
     */
    @AssertTrue(message = "{revolving.expenses.missing}")
    public boolean isExpensesDataGivenForRevolving() {
        boolean valid = true;

        if (isRevolving()) {
            if (creditLimitRequest.getFinance() != null && (creditLimitRequest.getFinance()
                    .getHousingCostsType() == null
                    || creditLimitRequest.getFinance()
                            .getMonthlyHousingCosts() == null
                    || creditLimitRequest.getFinance()
                            .getChildrenPresent() == null)) {
                valid = false;
            }
        }
        return valid;
    }

    ///////////////////////////////////////////////////////
    // EXTRA LOANS
    ///////////////////////////////////////////////////////

    /**
     * In case of a Charge playingcard, extra loans are not expected
     */
    @AssertTrue(message = "{charge.extraLoans.abundant}")
    public boolean isExtraLoansNotGivenForCharge() {
        boolean valid = true;

        if (isCharge()) {
            if (creditLimitRequest.getFinance() != null && !CollectionUtils.isEmpty(creditLimitRequest.getFinance()
                    .getExtraLoans())) {
                valid = false;
            }
        }
        return valid;
    }

    ///////////////////////////////////////////////////////
    // MARITAL STATUS
    ///////////////////////////////////////////////////////

    /**
     * In case of a Charge playingcard the marital status is not expected
     * 
     * @return boolean
     */
    @AssertTrue(message = "{charge.maritalStatus.abundant}")
    public boolean isMaritalStatusNotGivenForCharge() {
        boolean valid = true;

        if (isCharge()) {
            if (creditLimitRequest.getFinance() != null && creditLimitRequest.getFinance()
                    .getMaritalStatus() != null) {
                valid = false;
            }
        }
        return valid;
    }

    /**
     * In case of a Revolving playingcard the marital status is required
     * 
     * @return boolean
     */
    @AssertTrue(message = "{revolving.maritalStatus.missing}")
    public boolean isMaritalStatusGivenForRevolving() {
        boolean valid = true;

        if (isRevolving()) {
            if (creditLimitRequest.getFinance() != null && creditLimitRequest.getFinance()
                    .getMaritalStatus() == null) {
                valid = false;
            }
        }
        return valid;
    }

    // ---------------------------------------
    // Convenience methods below this line
    protected boolean isTemporary(SourceOfIncome sourceOfIncome) {
        return sourceOfIncome != null && sourceOfIncome.isTemporary();
    }

    // ---------------------------------------
    // Convenience methods below this line

    protected boolean isCharge() {
        return PortfolioCode.CHARGE.equals(creditLimitRequest.getPortfolioCode());
    }

    protected boolean isRevolving() {
        return PortfolioCode.REVOLVING.equals(creditLimitRequest.getPortfolioCode());
    }
}
