package applyextra.commons.action;

import lombok.extern.slf4j.Slf4j;
import applyextra.businessrules.RejectedRuleDTO;
import applyextra.businessrules.RuleEvaluationResult;
import applyextra.commons.dao.request.RejectedRulesService;
import applyextra.commons.model.database.entity.CreditCardRejectedRulesEntity;
import applyextra.commons.orchestration.Action;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class GetExceptionOverrulesAction implements Action<GetExceptionOverrulesAction.GetRejectedRulesDTO, RuleEvaluationResult> {

    @Resource
    private RejectedRulesService rejectedRulesService;

    @Override
    public RuleEvaluationResult perform(final GetRejectedRulesDTO dto) {
        List<RejectedRuleDTO> transformEntityToDto = new ArrayList<>();
        List<CreditCardRejectedRulesEntity> rejectedRules = rejectedRulesService.findByRequestId(dto.getRequestId());
        if (!rejectedRules.isEmpty()) {
            transformEntityToDto = transformEntityToDto(rejectedRules);
        }
        if (!transformEntityToDto.isEmpty() && transformEntityToDto.size() > 0) {
            dto.setRulesToReject(transformEntityToDto);
            return RuleEvaluationResult.OVERRULED;
        }
        return RuleEvaluationResult.PASSED;
    }

    private List<RejectedRuleDTO> transformEntityToDto(final List<CreditCardRejectedRulesEntity> rejectedRules) {
        List<RejectedRuleDTO> rejectedrulesList = new ArrayList<>();
        for (CreditCardRejectedRulesEntity rejectedRulesEntity : rejectedRules) {
            log.debug("rejected rule id:::" + rejectedRulesEntity.getRuleId() + "reject code:::"
                    + rejectedRulesEntity.getRejectCode() + "reject reason:::" + rejectedRulesEntity.getRejectReason());
            if (rejectedRulesEntity.isCanBeOverruled()) {
                RejectedRuleDTO rulesDTO = new RejectedRuleDTO(rejectedRulesEntity.getRuleId(),
                        rejectedRulesEntity.getRuleCheckingProcess(), rejectedRulesEntity.getRejectCode(),
                        rejectedRulesEntity.getRejectReason(), rejectedRulesEntity.isCanBeOverruled());
                rejectedrulesList.add(rulesDTO);
            }
        }
        return rejectedrulesList;
    }

    public interface GetRejectedRulesDTO {
        String getRequestId();

        void setRulesToReject(List<RejectedRuleDTO> rejectedRules);
    }
}
