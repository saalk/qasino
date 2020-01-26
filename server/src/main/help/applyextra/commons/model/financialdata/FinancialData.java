package applyextra.commons.model.financialdata;

import lombok.Getter;
import lombok.Setter;
import applyextra.commons.model.ExistingLoanAgreement;
import applyextra.commons.model.Loan;
import applyextra.commons.model.Person;
import applyextra.commons.model.SourceOfIncome;
import applyextra.commons.model.financialdata.creditlimit.CreditLimitRequest;
import nl.ing.sc.creditcardmanagement.commonobjects.PortfolioCode;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class FinancialData {

    private PortfolioCode portfolioCode;
    private BigDecimal requestedCreditLimit;

    private HousingCostsType housingCostsType;
    private BigDecimal monthlyHousingCosts;
    private MaritalStatus maritalStatus;
    private String startContractDate;
    private String endContractDate;
    private String timeSinceFreelancer;
    private Boolean costPaidWithING;

    private String accountNumber;
    private Integer accountType;

    private List<Loan> extraLoans;
    private List<ExistingLoanAgreement> existingLoans;

    private Person requester = new Person();

    public FinancialData() {
    }

    public FinancialData(String requestorId, CreditLimitRequest request) {
        setAllFromRequest(requestorId, request);

    }

    public void setAllFromRequest(final String requestorId, final CreditLimitRequest request) {
        portfolioCode = request.getPortfolioCode();
        setPartyId(requestorId);
        requestedCreditLimit = request.getRequestedCreditLimit();
        requester.setChildrenPresent(request.getFinance()
                .getChildrenPresent());
        requester.setIncomeFullLastYear(request.getFinance()
                .getIncomeFullLastYear());
        requester.setMonthlyAlimony(request.getFinance()
                .getMonthlyAlimony());
        requester.setMonthlyNetIncome(request.getFinance()
                .getMonthlyNetIncome());
        requester.setSourceOfIncome(request.getFinance()
                .getSourceOfIncome());
        extraLoans = request.getFinance()
                .getExtraLoans();
        housingCostsType = request.getFinance()
                .getHousingCostsType();
        maritalStatus = request.getFinance()
                .getMaritalStatus();
        monthlyHousingCosts = request.getFinance()
                .getMonthlyHousingCosts();
        startContractDate = request.getFinance()
                .getStartContractDate();
        endContractDate = request.getFinance()
                .getEndContractDate();
        timeSinceFreelancer = request.getFinance()
                .getTimeSinceFreelancer();
        costPaidWithING = request.getFinance()
                .getCostPaidWithING();
    }

    public String getPartyId() {
        return requester.getPartyId();
    }

    public Boolean getChildrenPresent() {
        return requester.getChildrenPresent();
    }

    public Boolean getIncomeFullLastYear() {
        return requester.getIncomeFullLastYear();
    }

    public BigDecimal getMonthlyAlimony() {
        return requester.getMonthlyAlimony();
    }

    public BigDecimal getMonthlyNetIncome() {
        return requester.getMonthlyNetIncome();
    }

    public SourceOfIncome getSourceOfIncome() {
        return requester.getSourceOfIncome();
    }

    public void setPartyId(final String requestorId) {
        requester.setPartyId(requestorId);
    }

}
