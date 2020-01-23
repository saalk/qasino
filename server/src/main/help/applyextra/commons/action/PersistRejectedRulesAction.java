package applyextra.commons.action;

import lombok.extern.slf4j.Slf4j;
import applyextra.businessrules.RejectedRuleDTO;
import applyextra.businessrules.RuleEvaluationResult;
import applyextra.commons.dao.request.RejectedRulesService;
import applyextra.commons.model.database.entity.CreditCardRequestEntity;
import applyextra.commons.orchestration.Action;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class PersistRejectedRulesAction implements Action<PersistRejectedRulesAction.PersistRejectedRulesDTO, RuleEvaluationResult> {

    @Resource
    private RejectedRulesService rejectedRulesService;

    @Override
    public RuleEvaluationResult perform(final PersistRejectedRulesDTO dto) {
        List<RejectedRuleDTO> rejectedRules = dto.getRejectedRules();
        if(!rejectedRules.isEmpty()){
            log.debug("list of rejected rules: " + rejectedRules.toString());
            rejectedRulesService.persistRejectedRulesEntity(dto);
            if (rejectedRules.stream()
                    .filter(rejectedRuleDTO -> !rejectedRuleDTO.isCanBeOverruled())
                    .findAny()
                    .isPresent()) {
                return RuleEvaluationResult.REJECTED;
            }
        }
        return RuleEvaluationResult.PASSED;
    }

    public interface PersistRejectedRulesDTO {
        List<RejectedRuleDTO> getRejectedRules();
        CreditCardRequestEntity getCurrentCreditCardRequest();
    }
}
