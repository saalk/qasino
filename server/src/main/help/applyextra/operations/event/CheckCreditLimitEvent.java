package applyextra.operations.event;

import lombok.extern.slf4j.Slf4j;
import applyextra.commons.activity.ActivityException;
import applyextra.commons.event.AbstractEvent;
import applyextra.commons.event.EventOutput;
import applyextra.commons.model.Loan;
import applyextra.commons.model.financialdata.FinancialData;
import applyextra.commons.model.financialdata.transformer.FinancialDataTransformer;
import applyextra.commons.model.financialdata.transformer.LoanTransformer;
import applyextra.commons.service.LoansService;
import nl.ing.riaf.ix.serviceclient.OKResult;
import nl.ing.riaf.ix.serviceclient.ServiceOperationResult;
import nl.ing.riaf.ix.serviceclient.ServiceOperationTask;
import nl.ing.sc.creditcardmanagement.checkrequestedcreditcardlimit1.CheckRequestedCreditCardLimitServiceOperationClient;
import nl.ing.sc.creditcardmanagement.checkrequestedcreditcardlimit1.value.CheckRequestedCreditCardLimitBusinessRequest;
import nl.ing.sc.creditcardmanagement.checkrequestedcreditcardlimit1.value.CheckRequestedCreditCardLimitBusinessResponse;
import org.joda.time.DateTime;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;

@Component
@Lazy
@Slf4j
@Deprecated
/**
 * @deprecated use this event only for old data table
 */
public class CheckCreditLimitEvent extends AbstractEvent {
    private static final String SERVICE_NAME = "CreditLimit";

    @Resource
    private CheckRequestedCreditCardLimitServiceOperationClient operationClient;
    @Resource
    private LoansService loansService;

    @Override
    protected EventOutput execution(final Object... eventInput) {
        
        final CheckCreditLimitDTO flowDTO = (CheckCreditLimitDTO) eventInput[0];
        final FinancialData financialData = flowDTO.getFinancialData();

        final CheckRequestedCreditCardLimitBusinessRequest businessRequest = populateCreditLimitBusinessRequest(financialData);

        addExtraLoans(financialData, DateTime.now()
                .plusMinutes(loansService.getExpireInterval()));

        ServiceOperationTask taskNew = operationClient.execute(businessRequest);
        ServiceOperationResult result = taskNew.getResult(false);

        if (result.isOk()) {
            OKResult okResult = result.getOk();
            CheckRequestedCreditCardLimitBusinessResponse response = (CheckRequestedCreditCardLimitBusinessResponse) okResult
                    .getResponse();

            flowDTO.setResult(response.getAllowed());

            // the requested credit limit is not applicable, suggested the applicable limit to user
            if (!response.getAllowed() && response.getMaxLimitAllowed() != null) {
                flowDTO.setMaxLimitAllowed(response.getMaxLimitAllowed());
            }

        } else {
            log.debug("Error when doing credit limit checks for customer " + businessRequest.getPartyId());
            throw new ActivityException(SERVICE_NAME, Math.abs(result.getError()
                    .getErrorCode()), // in case it's a negative error code which riaf does not accept
                    "Error when doing credit limit checks for customer " + businessRequest.getPartyId(), result.getError()
                            .getException());
        }
        return EventOutput.success();
    }

    /**
     * @param financialData
     * @return CheckRequestedCreditCardLimitBusinessRequest
     */
    private CheckRequestedCreditCardLimitBusinessRequest populateCreditLimitBusinessRequest(final FinancialData financialData) {
        
        final CheckRequestedCreditCardLimitBusinessRequest businessRequest = new CheckRequestedCreditCardLimitBusinessRequest();
        businessRequest.setChildrenPresent(financialData.getChildrenPresent());
        businessRequest.setExistingLoans(LoanTransformer.loans2Agreements(financialData.getExistingLoans()));
        businessRequest.setExtraLoans(
                FinancialDataTransformer.transformLoans(financialData.getExtraLoans(), financialData.getRequester()));
        businessRequest
                .setHousingCostsType(FinancialDataTransformer.transformHousingCostsType(financialData.getHousingCostsType()));
        businessRequest.setIncomeFullLastYear(financialData.getIncomeFullLastYear());
        businessRequest.setMaritalStatus(FinancialDataTransformer.transformMaritalStatus(financialData.getMaritalStatus()));
        businessRequest.setMonthlyAlimony(financialData.getMonthlyAlimony());
        businessRequest.setMonthlyHousingCosts(financialData.getMonthlyHousingCosts());
        businessRequest.setMonthlyNetIncome(financialData.getMonthlyNetIncome());
        businessRequest.setPartyId(financialData.getPartyId());
        businessRequest.setPortfolioCode(financialData.getPortfolioCode());

        businessRequest.setRequestedCreditLimit(financialData.getRequestedCreditLimit());
        businessRequest.setSourceOfIncome(FinancialDataTransformer.transformSourceOfIncome(financialData.getSourceOfIncome()));
        return businessRequest;
    }

    private void addExtraLoans(final FinancialData financialData, final DateTime expireTime) {
        
        if (financialData.getExtraLoans() != null && !financialData.getExtraLoans().isEmpty()) {
            for (Loan loan : financialData.getExtraLoans()) {
                loan.setExpireTime(expireTime.toDate());
            }
            loansService.updateExtraLoans(financialData.getExtraLoans());
        }

        financialData.getRequester().setLastUpdated(DateTime.now().toDate());       
        
    }

    public interface CheckCreditLimitDTO {
        
        FinancialData getFinancialData();

        void setResult(boolean result);

        void setMaxLimitAllowed(BigDecimal maxLimitAllowed);

    }
}
