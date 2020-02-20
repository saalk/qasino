package applyextra.commons.action;

import lombok.extern.slf4j.Slf4j;
import applyextra.commons.dao.request.CreditcardRequestService;
import applyextra.commons.event.AbstractCreditcardFlowDTO;
import applyextra.commons.model.database.entity.CreditCardRequestEntity;
import applyextra.commons.model.financialdata.dto.FinancialAcceptanceDTO;
import applyextra.commons.orchestration.Action;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static applyextra.commons.orchestration.EventEnum.ENTER_STATE;

@Component
@Slf4j
public class LoadCreditcardRequestContextAction implements Action<AbstractCreditcardFlowDTO, Boolean> {

    @Resource
    private CreditcardRequestService ccRequestService;
    @Resource
    private LoadPartyFinancialDataAction financialDataAction;

    @Override
    public Boolean perform(final AbstractCreditcardFlowDTO dto) {
        //request does not have to be loaded again if next state is processed in succession
        if (ENTER_STATE != dto.getCurrentEvent()) {

            CreditCardRequestEntity request;

            if (dto.getRequestId() == null) {
                request = ccRequestService.createCreditcardRequest(dto);
            } else {
                request = ccRequestService.getCreditcardRequest(dto.getRequestId());
            }
            dto.addCreditcardRequest(request);

            //If the dto contains financialacceptance dto, also load that data
            if (dto instanceof FinancialAcceptanceDTO) {
                financialDataAction.perform((FinancialAcceptanceDTO) dto);
                if (request.getAccount() != null && request.getAccount().getAccountStatus() != null) {
                    ((FinancialAcceptanceDTO) dto).setPortfolioCode(request.getAccount().getAccountStatus().getPortfolioCode());
                }
            }
            dto.injectFrontendRequestInput();
        }
        return true;
    }

}
