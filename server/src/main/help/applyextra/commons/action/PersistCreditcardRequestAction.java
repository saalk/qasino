package applyextra.commons.action;

import applyextra.commons.dao.request.CreditcardRequestService;
import applyextra.commons.event.AbstractCreditcardFlowDTO;
import applyextra.commons.model.database.entity.CreditCardRequestEntity;
import applyextra.commons.model.financialdata.dto.FinancialAcceptanceDTO;
import applyextra.commons.orchestration.Action;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Lazy
@Component
public class PersistCreditcardRequestAction implements Action<AbstractCreditcardFlowDTO, Boolean> {

    @Resource
    private CreditcardRequestService requestService;
    @Resource
    private PersistPartyFinancialDataAction persistPartyFinancialDataAction;

    @Override
    public Boolean perform(final AbstractCreditcardFlowDTO dto) {
        CreditCardRequestEntity creditCardRequestEntity = dto.getCreditcardRequest();
        // If the dto contains FinancialAcceptanceDTO, persist that load as well
        if (dto instanceof FinancialAcceptanceDTO) {
            persistPartyFinancialDataAction.perform((FinancialAcceptanceDTO) dto);
            creditCardRequestEntity.setFinancialData(((FinancialAcceptanceDTO) dto).getFinancialData());
        }
        dto.addCreditcardRequest(requestService.updateRequest(dto.getCreditcardRequest()));
        return true;
    }
}
