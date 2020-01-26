package applyextra.commons.action;

import lombok.extern.slf4j.Slf4j;
import applyextra.businessrules.KandatRule;
import applyextra.businessrules.OverallRuleEvaluationResponse;
import applyextra.businessrules.RuleEvaluationResult;
import applyextra.businessrules.RuleEvaluationSettings;
import applyextra.commons.orchestration.Action;
import applyextra.commons.resource.ErrorCodes;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Map;

import static applyextra.businessrules.RuleEvaluationResult.PASSED;
import static applyextra.businessrules.RuleEvaluationResult.REJECTED;


@Component
@Slf4j
@Lazy
public class CheckAction implements Action<CheckAction.CheckActionDTO, RuleEvaluationResult> {

    /**
     * call business rule for rule check for the invoking process linkedhashmap is used to gurantee the ordering as some business
     * call SIA and we want ordering of business rules to be governed by api
     */
    @Override
    public RuleEvaluationResult perform(final CheckActionDTO flowDTO) {
        Map<KandatRule, Object> rulesMap = flowDTO.getRulesMap();
        OverallRuleEvaluationResponse evaluationResponse = new OverallRuleEvaluationResponse();
        RuleEvaluationResult result = PASSED;
        ErrorCodes firstEncounteredRejectionCode = null;
        for (final Map.Entry<KandatRule, Object> entry : rulesMap.entrySet()) {
            final KandatRule rule = entry.getKey();
            if (!rule.isEnabled()) {
                log.debug("{} Rule disabled.", rule);
                continue;
            }

            final boolean ruleStatus = rule.evaluate(entry.getValue());

            if (!ruleStatus) {
                result = REJECTED;
                flowDTO.setRulesCode(rule.getErrorCode());
                log.debug("Business rule " + rule.getClass().getSimpleName() + " results in rejection");
                if (firstEncounteredRejectionCode == null) {
                    firstEncounteredRejectionCode = rule.getActualErrorCode();
                }
                if (flowDTO.stopAfterRejection()) {
                    evaluationResponse.markRejected();
                    break;
                } else {
                    evaluationResponse.reject(rule, "" + rule.getErrorCode(), "Business rule " + rule.getClass().getSimpleName() + " results in rejection");
                }
            }
        }
        if (result == REJECTED) {
            flowDTO.setEvaluationResponse(firstEncounteredRejectionCode, evaluationResponse);
        }
        return result;
    }

    public interface CheckActionDTO {
        Map<KandatRule, Object> getRulesMap();

        boolean stopAfterRejection();

        RuleEvaluationSettings getRuleEvaluationSettings();

        void setEvaluationResponse(final ErrorCodes overallErrorCode, final OverallRuleEvaluationResponse response);

        void setRulesCode(final Integer code);
    }
}
