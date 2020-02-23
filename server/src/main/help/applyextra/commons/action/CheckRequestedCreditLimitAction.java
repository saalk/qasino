package applyextra.commons.action;

import lombok.extern.slf4j.Slf4j;
import applyextra.commons.activity.ActivityException;
import applyextra.commons.event.EventOutput;
import applyextra.commons.model.database.entity.ExistingLoanEntity;
import applyextra.commons.model.database.entity.ExtraLoanEntity;
import applyextra.commons.model.database.entity.FinancialDataEntity;
import applyextra.commons.model.database.entity.PartyEntity;
import applyextra.commons.orchestration.Action;
import nl.ing.riaf.ix.serviceclient.OKResult;
import nl.ing.riaf.ix.serviceclient.ServiceOperationResult;
import nl.ing.riaf.ix.serviceclient.ServiceOperationTask;
import nl.ing.sc.creditcardmanagement.checkcreditcardcreditscore1.jaxb.generated.CheckCreditCardCreditScore001Reply;
import nl.ing.sc.creditcardmanagement.checkrequestedcreditcardlimit1.CheckRequestedCreditCardLimitServiceOperationClient;
import nl.ing.sc.creditcardmanagement.checkrequestedcreditcardlimit1.value.CheckRequestedCreditCardLimitBusinessRequest;
import nl.ing.sc.creditcardmanagement.checkrequestedcreditcardlimit1.value.CheckRequestedCreditCardLimitBusinessResponse;
import nl.ing.sc.creditcardmanagement.commonobjects.Loan;
import nl.ing.sc.creditcardmanagement.commonobjects.PortfolioCode;
import org.joda.time.LocalDate;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
@Lazy
@Slf4j
public class CheckRequestedCreditLimitAction implements Action<CheckRequestedCreditLimitAction.CheckRequestedCreditLimitDTO, EventOutput.Result> {
    private static final String SERVICE_NAME = "CheckRequestedCreditCardLimit";

    @Resource
    private CheckRequestedCreditCardLimitServiceOperationClient checkRequestedCreditCardLimit;

    @Override
    public EventOutput.Result perform(CheckRequestedCreditLimitDTO flowDTO) {
        final CheckRequestedCreditCardLimitBusinessRequest businessRequest = populateCreditLimitBusinessRequest(flowDTO);

        ServiceOperationTask task = checkRequestedCreditCardLimit.execute(businessRequest);
        ServiceOperationResult result = task.getResult();
        if (result.isOk()) {
            OKResult okResult =

                    result.getOk();
            CheckRequestedCreditCardLimitBusinessResponse response = (CheckRequestedCreditCardLimitBusinessResponse) okResult
                    .getResponse();

            if (flowDTO.isCreditLimitResult()) {
                flowDTO.setCreditLimitResult(response.getAllowed());
            }
            if (response.getAllowed()) {
                flowDTO.setCreditLimit(businessRequest.getRequestedCreditLimit().intValueExact());
            }
            // the requested credit limit is not applicable, suggested the applicable limit to user
            else if (!response.getAllowed() && response.getMaxLimitAllowed() != null) {
                flowDTO.setMaxLimitAllowed(response.getMaxLimitAllowed());
                log.warn("Request with Id " + flowDTO.getRequestId() + " has been rejected in the CalcLimit, allowed limit is " + response.getMaxLimitAllowed());
            }

        } else {
            log.debug("Error in tibco service CheckRequestedCreditCardLimit for partyId " + flowDTO.getPartyEntity().getPartyId());
            throw new ActivityException(SERVICE_NAME, Math.abs(result.getError()
                    .getErrorCode()), // in case it's a negative error code which riaf does not accept
                    "Error in tibco service CheckRequestedCreditCardLimit: " + flowDTO.getPartyEntity().getPartyId(), result.getError()
                    .getException());
        }

        return EventOutput.Result.SUCCESS;
    }

    private static CheckRequestedCreditCardLimitBusinessRequest populateCreditLimitBusinessRequest(final CheckRequestedCreditLimitDTO flowDTO) {
        final CheckRequestedCreditCardLimitBusinessRequest businessRequest = new CheckRequestedCreditCardLimitBusinessRequest();
        businessRequest.setPartyId(flowDTO.getPartyEntity().getPartyId());
        businessRequest.setPortfolioCode(flowDTO.getPortfolioCode());
        businessRequest.setRequestedCreditLimit(new BigDecimal(flowDTO.getRequestedCreditLimit()));
        if (PortfolioCode.REVOLVING.equals(flowDTO.getPortfolioCode())) {
            businessRequest.setSourceOfIncome(flowDTO.getFinancialData().getIncomeSource());
            businessRequest.setMonthlyNetIncome(flowDTO.getFinancialData().getMonthlyNetIncome());
            businessRequest.setIncomeFullLastYear(flowDTO.getFinancialData().getIncomeFullLastYear());
            businessRequest.setMonthlyAlimony(flowDTO.getFinancialData().getMonthlyAlimony());
            businessRequest.setHousingCostsType(flowDTO.getFinancialData().getHousingCostsType());
            businessRequest.setMonthlyHousingCosts(flowDTO.getFinancialData().getMonthlyHousingCosts());
            businessRequest.setChildrenPresent(flowDTO.getFinancialData().getChildrenPresent());
            businessRequest.setMaritalStatus(flowDTO.getFinancialData().getMaritalStatus());
            businessRequest.setExtraLoans(mapLoans(flowDTO));
        }
        businessRequest.setExistingLoans(mapExistingLoans(flowDTO));

        return businessRequest;
    }

    private static List<Loan> mapLoans(final CheckRequestedCreditLimitDTO flowDTO) {
        List<Loan> loans = new ArrayList<>();
        for (ExtraLoanEntity extraLoanEntity : flowDTO.getFinancialData().getExtraLoanEntities()) {
            Loan loan = new Loan(extraLoanEntity.getTotalAmount(),
                    extraLoanEntity.getMonthlyAmount(),
                    extraLoanEntity.getCounterParty());
            loans.add(loan);
        }
        return loans;
    }

    private static List<CheckCreditCardCreditScore001Reply.LoanAgreement> mapExistingLoans(final CheckRequestedCreditLimitDTO flowDTO) {
        List<CheckCreditCardCreditScore001Reply.LoanAgreement> loanAgreements = new ArrayList<>();
        for (ExistingLoanEntity existingLoanEntity : flowDTO.getPartyEntity().getExistingLoanEntities()) {
            CheckCreditCardCreditScore001Reply.LoanAgreement loanAgreement = new CheckCreditCardCreditScore001Reply.LoanAgreement();
            loanAgreement.setAgreementNumber(existingLoanEntity.getAgreementNumber());
            loanAgreement.setAgreementType(existingLoanEntity.getAgreementType());
            loanAgreement.setCalcFinalDownPaymentDate(new LocalDate(existingLoanEntity.getCalcFinalDownPaymentDate()));
            loanAgreement.setCreditLimit(existingLoanEntity.getOldCreditLimit());
            loanAgreement.setFirstDownPaymentDate(new LocalDate(existingLoanEntity.getFirstDownPaymentDate()));
            loanAgreement.setRealFinalDownPaymentDate(new LocalDate(existingLoanEntity.getRealFinalDownPaymentDate()));
            loanAgreement.setHoldership(existingLoanEntity.getHoldership());
            loanAgreement.setRemark(existingLoanEntity.getRemark());

            loanAgreements.add(loanAgreement);
        }
        return loanAgreements;
    }


    public interface CheckRequestedCreditLimitDTO {
        PartyEntity getPartyEntity();

        boolean isCreditLimitResult();

        FinancialDataEntity getFinancialData();

        PortfolioCode getPortfolioCode();

        Integer getRequestedCreditLimit();

        void setCreditLimit(Integer creditLimit);

        void setCreditLimitResult(boolean result);

        void setMaxLimitAllowed(BigDecimal maxLimitAllowed);

        String getRequestId();
    }


}
