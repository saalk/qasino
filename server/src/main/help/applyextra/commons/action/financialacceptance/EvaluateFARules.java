package applyextra.commons.action.financialacceptance;

import applyextra.businessrules.FinancialAcceptanceRule;
import applyextra.businessrules.OverallRuleEvaluationResponse;
import applyextra.businessrules.RuleEvaluationResult;
import applyextra.commons.action.CheckRequestedCreditLimitAction;
import applyextra.commons.model.database.entity.DecisionScoreEntity;
import applyextra.commons.orchestration.Action;
import applyextra.commons.resource.ErrorCodes;
import org.springframework.stereotype.Component;

import java.util.List;

import static applyextra.businessrules.FinancialAcceptanceRule.LIMIT_CHECK;
import static applyextra.commons.model.CreditScoreResult.GREEN;

/**
 * Created by CL94WQ on 17-10-2017.
 */
@Component
public class EvaluateFARules implements Action<EvaluateFARules.EvaluateFARulesDTO, RuleEvaluationResult> {
    @Override
    public RuleEvaluationResult perform(final EvaluateFARulesDTO financialAcceptanceDTO) {

        List<DecisionScoreEntity> decisionScores = financialAcceptanceDTO.getPartyEntity().getDecisionScoreEntities();
        if (decisionScores.isEmpty()) {
            throw new IllegalArgumentException("Could not find decision scores to determine overall scoring");
        }
        ErrorCodes errorCode = ErrorCodes.NONE;
        OverallRuleEvaluationResponse ruleEvaluationResponse = new OverallRuleEvaluationResponse();
        //Only check decision score evaluation if this does not involve limit check, else do limit check evaluation
        if (financialAcceptanceDTO.getRequestedCreditLimit() == null ) {
            for (DecisionScoreEntity decisionScore : decisionScores) {
                if (decisionScore.getResult() != GREEN) {
                    errorCode = ErrorCodes.FINANCIAL_ACC_DECLINED;
                    switch (decisionScore.getType()) {
                        case BKRSCORE: {
                            ruleEvaluationResponse.reject(FinancialAcceptanceRule.getRuleByType(decisionScore.getType()),
                                    "" + ErrorCodes.BKR_CHECK.getStatus(),
                                    decisionScore.getReason());
                            break;
                        }
                        case CREDITSCORE: {
                            ruleEvaluationResponse.reject(FinancialAcceptanceRule.getRuleByType(decisionScore.getType()),
                                    "" + ErrorCodes.SCORING_FAILED.getStatus(),
                                    decisionScore.getReason());
                            break;
                        }
                    }
                }
            }
        } else if (!financialAcceptanceDTO.isCreditLimitResult()) {
            ruleEvaluationResponse.reject(LIMIT_CHECK,
                    "" + ErrorCodes.CREDIT_LIMIT_NOT_ALLOWED.getStatus(),
                    Boolean.toString(financialAcceptanceDTO.isCreditLimitResult()));
        }
        financialAcceptanceDTO.setEvaluationResponse(errorCode, ruleEvaluationResponse);
        return ruleEvaluationResponse.getResult();
    }

    public interface EvaluateFARulesDTO extends CheckRequestedCreditLimitAction.CheckRequestedCreditLimitDTO {
        void setEvaluationResponse(final ErrorCodes overallErrorCode, final OverallRuleEvaluationResponse response);
    }
}
