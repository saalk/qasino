package applyextra.commons.action;

import lombok.extern.slf4j.Slf4j;
import applyextra.commons.dao.request.PartyEntityService;
import applyextra.commons.event.EventOutput;
import applyextra.commons.model.database.entity.DecisionScoreEntity;
import applyextra.commons.model.financialdata.dto.FinancialAcceptanceDTO;
import applyextra.commons.orchestration.Action;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
@Slf4j
public class PersistPartyFinancialDataAction implements Action<FinancialAcceptanceDTO, EventOutput.Result> {

    @Resource
    private PartyEntityService partyService;

    @Override
    public EventOutput.Result perform(final FinancialAcceptanceDTO dto) {
        for (DecisionScoreEntity decisionScoreEntity : dto.getPartyEntity().getDecisionScoreEntities()) {
            if (isBlank(decisionScoreEntity.getRequestId())) {
                decisionScoreEntity.setRequestId(dto.getRequestId());
            }
        }
        partyService.persistPartyEntity(dto);
        return EventOutput.Result.SUCCESS;
    }

}
