package applyextra.commons.dao.request;

import lombok.extern.slf4j.Slf4j;
import applyextra.businessrules.RejectedRuleDTO;
import applyextra.commons.action.PersistRejectedRulesAction;
import applyextra.commons.model.database.entity.CreditCardRejectedRulesEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@Lazy
public class RejectedRulesService {

    @Autowired
    private RejectedRulesRepository repository;

    @Transactional
    public void persistRejectedRulesEntity(PersistRejectedRulesAction.PersistRejectedRulesDTO dto) {
        List<CreditCardRejectedRulesEntity> rejectedRules = new ArrayList<>();
        List<CreditCardRejectedRulesEntity> rejectedRulesInDb = repository.findByRequest_Id(dto.getCurrentCreditCardRequest()
                .getId());

        for (RejectedRuleDTO rejectedRulesDTO : dto.getRejectedRules()) {
            log.debug("rejected rule id:::" + rejectedRulesDTO.getRuleId() + "reject code:::" + rejectedRulesDTO.getRejectCode()
                    + "reject reason:::" + rejectedRulesDTO.getRejectReason());
            // TODO check if this is enough to have unique values in DB
            if (rejectedRulesDTO != null && !rejectedRulesInDb.isEmpty()) {
                for (CreditCardRejectedRulesEntity rejectedRulesInDbEntity : rejectedRulesInDb) {
                    if (!rejectedRulesInDbEntity.getRuleId()
                            .equals(rejectedRulesDTO.getRuleId())
                            && !rejectedRulesInDbEntity.getRejectCode()
                                    .equals(rejectedRulesDTO.getRejectCode())) {
                        if (rejectedRulesDTO.getRejectReason() != null && !rejectedRulesInDbEntity.getRejectReason()
                                .equals(rejectedRulesDTO.getRejectReason())) {
                            transformDtoToEntity(dto, rejectedRules, rejectedRulesDTO);
                        }

                    }
                }
            } else {
                transformDtoToEntity(dto, rejectedRules, rejectedRulesDTO);
            }
        }
        repository.save(rejectedRules);
    }

    private void transformDtoToEntity(PersistRejectedRulesAction.PersistRejectedRulesDTO dto,
            List<CreditCardRejectedRulesEntity> rejectedRules, RejectedRuleDTO rejectedRulesDTO) {
        CreditCardRejectedRulesEntity rulesEntity = new CreditCardRejectedRulesEntity();
        rulesEntity.setRuleId(rejectedRulesDTO.getRuleId());
        rulesEntity.setRejectCode(rejectedRulesDTO.getRejectCode());
        rulesEntity.setRejectReason(rejectedRulesDTO.getRejectReason());
        rulesEntity.setRuleCheckingProcess(rejectedRulesDTO.getRuleCheckingProcess());
        rulesEntity.setRequest(dto.getCurrentCreditCardRequest());
        rejectedRules.add(rulesEntity);
    }

    @Transactional(readOnly = true)
    public List<CreditCardRejectedRulesEntity> findByRequestId(String requestId) {
        List<CreditCardRejectedRulesEntity> request = repository.findByRequest_Id(requestId);
        return request;
    }

    @Transactional
    public void overrulePermittedRejectedRules(List<CreditCardRejectedRulesEntity> rulesToOverrule) {
        repository.save(rulesToOverrule);
    }
}
