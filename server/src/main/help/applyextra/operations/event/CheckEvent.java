package applyextra.operations.event;

import lombok.extern.slf4j.Slf4j;
import applyextra.businessrules.KandatRule;
import applyextra.commons.event.AbstractEvent;
import applyextra.commons.event.EventOutput;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
@Lazy
@Deprecated
public class CheckEvent extends AbstractEvent {

    /**
     * call business rule for rule check for the invoking process linkedhashmap is used to gurantee the ordering as some
     * business call SIA and we want ordering of business rules to be governed by api
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected EventOutput execution(final Object... eventInput) {

        final CheckEventDTO flowDTO = (CheckEventDTO) eventInput[0];
        Map<KandatRule, Object> rulesMap = flowDTO.getRulesMap();
        EventOutput.Result result = EventOutput.Result.SUCCESS;
        flowDTO.setRulesCode(0);
        for (final Map.Entry<KandatRule, Object> entry : rulesMap.entrySet()) {
            final KandatRule rule = entry.getKey();
            if (!rule.isEnabled()) {
                log.debug("{} Rule disabled.", rule);
                continue;
            }

            final boolean ruleStatus = rule.evaluate(entry.getValue());

            if (!ruleStatus) {
                result = EventOutput.Result.FAILURE;
                log.warn("Request with Id " + flowDTO.getRequestId() + " was declined by Business rule " + rule.getClass().getSimpleName());
                flowDTO.setRulesCode(rule.getErrorCode());
                break;
            }
        }

        return new EventOutput(result);
    }

    public interface CheckEventDTO {
        Map<KandatRule, Object> getRulesMap();

        void setRulesCode(final Integer code);

        String getRequestId();
    }

}
