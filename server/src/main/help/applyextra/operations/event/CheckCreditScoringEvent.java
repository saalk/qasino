package applyextra.operations.event;

import lombok.extern.slf4j.Slf4j;
import applyextra.commons.activity.ActivityException;
import applyextra.commons.event.AbstractEvent;
import applyextra.commons.event.EventOutput;
import applyextra.commons.model.DecisionScore;
import applyextra.commons.model.ExistingLoanAgreement;
import applyextra.commons.model.Loan;
import applyextra.commons.model.financialdata.FinancialData;
import applyextra.commons.model.financialdata.transformer.LoanTransformer;
import applyextra.commons.service.DecisionScoreService;
import applyextra.commons.service.LoansService;
import nl.ing.riaf.ix.serviceclient.OKResult;
import nl.ing.riaf.ix.serviceclient.ServiceOperationResult;
import nl.ing.riaf.ix.serviceclient.ServiceOperationTask;
import nl.ing.sc.creditcardmanagement.checkcreditcardcreditscore1.CheckCreditCardCreditScoreServiceOperationClient;
import nl.ing.sc.creditcardmanagement.checkcreditcardcreditscore1.jaxb.generated.CheckCreditCardCreditScore001Reply;
import nl.ing.sc.creditcardmanagement.checkcreditcardcreditscore1.value.CheckCreditCardCreditScoreBusinessRequest;
import nl.ing.sc.creditcardmanagement.checkcreditcardcreditscore1.value.CheckCreditCardCreditScoreBusinessResponse;
import nl.ing.sc.creditcardmanagement.checkcreditcardcreditscore1.value.CreditScoreResult;
import nl.ing.sc.creditcardmanagement.checkcreditcardcreditscore1.value.SubScore;
import org.joda.time.DateTime;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static applyextra.commons.event.EventOutput.Result.FAILURE;
import static applyextra.commons.state.CreditCardsStateMachine.Trigger.NOT_OK;

@Component
@Lazy
@Slf4j
public class CheckCreditScoringEvent extends AbstractEvent {

    private static final String SERVICE_NAME = "CreditScoring";

    @Resource
    private CheckCreditCardCreditScoreServiceOperationClient operationClient;

    @Resource
    private LoansService loansService;

    @Resource
    private DecisionScoreService decisionScoreService;

    @Override
    protected EventOutput execution(final Object... eventInput) {
        final CheckCreditScoringEventDTO flowDTO = (CheckCreditScoringEventDTO) eventInput[0];
        final DateTime expireTime = DateTime.now()
                .plusMinutes(loansService.getExpireInterval());

        if (flowDTO.getCreditScoreResult() == null || flowDTO.getCreditScoreResult() == CreditScoreResult.MISSING) {
            updateLoanInformation(flowDTO, expireTime);
        } else {
            getLoansFromDatabase(flowDTO, expireTime);
        }

        if (CreditScoreResult.GREEN != flowDTO.getCreditScoreResult()) {
            return new EventOutput(FAILURE, NOT_OK);
        } else {
            return EventOutput.success();
        }
    }

    private void updateLoanInformation(final CheckCreditScoringEventDTO flowDTO, final DateTime expireTime) {

        final FinancialData financialData = flowDTO.getFinancialData();
        final CheckCreditCardCreditScoreBusinessRequest businessRequest = populateCreditScoreBusinessRequest(financialData);

        ServiceOperationTask taskNew = operationClient.execute(businessRequest);
        ServiceOperationResult result = taskNew.getResult(false);

        if (result.isOk()) {
            OKResult okResult = result.getOk();
            CheckCreditCardCreditScoreBusinessResponse response = (CheckCreditCardCreditScoreBusinessResponse) okResult
                    .getResponse();

            addLoansFromResponse(response.getLoanAgreements(), financialData, expireTime);
            addDecisionScoreinDatabase(response.getSubScore(), financialData.getRequester()
                    .getPartyId());
            flowDTO.setCreditScoreResult(response.getResult());
        } else {
            log.debug("Error when doing credit score checks for customer " + businessRequest.getPartyId());
            throw new ActivityException(SERVICE_NAME, Math.abs(result.getError()
                    .getErrorCode()), // in case it's a negative error code which riaf does not accept
                    "Error when doing credit score checks for customer " + businessRequest.getPartyId(), result.getError()
                            .getException());
        }
    }

    private void addDecisionScoreinDatabase(List<SubScore> subScores, final String partyId) {

        List<DecisionScore> decisionScores = new ArrayList<>();

        for (SubScore score : subScores) {
            DecisionScore decisionScore = new DecisionScore();

            if (decisionScore.getId() == null) {
                decisionScore.setId(UUID.randomUUID()
                        .toString());
            }

            decisionScore.setLastUpdated(new Date());
            decisionScore.setName(score.getName()
                    .toString());
            decisionScore.setReason(score.getReason());
            decisionScore.setResult(score.getResult());
            decisionScore.setPersonId(partyId);
            decisionScores.add(decisionScore);
        }
        decisionScoreService.updateDecisionScore(decisionScores);
    }

    /**
     * @param financialData
     * @return CheckCreditCardCreditScoreBusinessRequest
     */
    private CheckCreditCardCreditScoreBusinessRequest populateCreditScoreBusinessRequest(final FinancialData financialData) {

        final CheckCreditCardCreditScoreBusinessRequest businessRequest = new CheckCreditCardCreditScoreBusinessRequest();
        businessRequest.setAccountNumber(financialData.getAccountNumber());
        businessRequest.setPartyId(financialData.getPartyId());
        businessRequest.setAccountType(financialData.getAccountType());
        businessRequest.setPortfolioCode(financialData.getPortfolioCode());
        businessRequest.setRequestedCreditLimit(financialData.getRequestedCreditLimit());

        return businessRequest;
    }

    private void getLoansFromDatabase(final CheckCreditScoringEventDTO flowDTO, final DateTime expireTime) {

        final FinancialData financialData = flowDTO.getFinancialData();
        final List<ExistingLoanAgreement> tempExistingLoans = loansService.getExistingLoans(financialData.getRequester()
                .getId());
        if (!tempExistingLoans.isEmpty()) {
            if (expireTime.isBefore(new DateTime(tempExistingLoans.get(0)
                    .getExpireTime()))) {
                updateLoanInformation(flowDTO, expireTime);
            } else {
                financialData.setExistingLoans(tempExistingLoans);
            }
        }
        financialData.setExtraLoans(loansService.getExtraLoans(financialData.getRequester()
                .getId()));
    }

    private void addLoansFromResponse(final List<CheckCreditCardCreditScore001Reply.LoanAgreement> loanAgreements,
            final FinancialData financialData, final DateTime expireTime) {

        financialData.setExistingLoans(LoanTransformer.agreements2Loans(loanAgreements));

        if (financialData.getExistingLoans() != null && !financialData.getExistingLoans()
                .isEmpty()) {
            for (ExistingLoanAgreement loan : financialData.getExistingLoans()) {
                log.debug("existing loans requestor partyId: " + financialData.getRequester()
                        .getPartyId());
                loan.setPersonId(financialData.getRequester()
                        .getPartyId());
                loan.setExpireTime(expireTime.toDate());
            }
            loansService.updateExistingLoans(financialData.getExistingLoans());
        }

        if (financialData.getExtraLoans() != null && !financialData.getExtraLoans()
                .isEmpty()) {
            for (Loan loan : financialData.getExtraLoans()) {
                loan.setPersonId(financialData.getRequester()
                        .getId());
                loan.setExpireTime(expireTime.toDate());
            }
            loansService.updateExtraLoans(financialData.getExtraLoans());
        }

        financialData.getRequester()
                .setLastUpdated(DateTime.now()
                        .toDate());

    }

    public interface CheckCreditScoringEventDTO {

        FinancialData getFinancialData();

        CreditScoreResult getCreditScoreResult();

        void setCreditScoreResult(CreditScoreResult result);

    }

}
