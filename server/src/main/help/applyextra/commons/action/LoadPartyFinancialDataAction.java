package applyextra.commons.action;

import lombok.extern.slf4j.Slf4j;
import applyextra.commons.dao.request.PartyEntityService;
import applyextra.commons.model.financialdata.dto.FinancialAcceptanceDTO;
import applyextra.commons.orchestration.Action;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class LoadPartyFinancialDataAction implements Action<FinancialAcceptanceDTO,Boolean> {

    @Resource
    private PartyEntityService partyEntityService;

    @Override
    public Boolean perform(final FinancialAcceptanceDTO dto) {
        String partyId = dto.getCustomerId();
        if (partyId == null) {
            partyId = dto.getCreditcardRequest().getCustomerId();
        }
        dto.setPartyEntity(partyEntityService.getPartyEntity(partyId,dto.getRequestId()));
        //Add the request to the set of requests
        return true;
    }



}
