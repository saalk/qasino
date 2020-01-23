package applyextra.operations.activity;

import lombok.extern.slf4j.Slf4j;
import applyextra.businessrules.KandatRule;
import applyextra.commons.activity.AbstractActivity;
import applyextra.commons.activity.ActivityOutput;
import applyextra.operations.dto.CheckResponseDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
@Slf4j
@Lazy
public class CheckActivity extends AbstractActivity<CheckResponseDTO> {

    /**
     * call business rule for rule check for the invoking process
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected ActivityOutput<CheckResponseDTO> execution(final Object... activityInput) {
        Map<KandatRule, Object> rulesMap = (LinkedHashMap<KandatRule, Object>) activityInput[0];
        CheckResponseDTO responseDTO = new CheckResponseDTO();
        ActivityOutput.Result result = ActivityOutput.Result.SUCCESS;

        for (Map.Entry<KandatRule, Object> entry : rulesMap.entrySet()) {
            KandatRule rule = entry.getKey();
            if(!rule.isEnabled()) {
                log.debug("{} Rule disabled.", rule);
                continue;
            }

            boolean ruleStatus = rule.evaluate(entry.getValue());

            log.debug("{} Rule evaluation result:----> {}", rule, ruleStatus);

            result = ActivityOutput.Result.SUCCESS;

            if (!ruleStatus) {
                result = ActivityOutput.Result.FAILURE;
                responseDTO.setErrorCode(rule.getErrorCode());
                break;
            }
        }

        return new ActivityOutput<>(result, responseDTO);
    }

}
