package applyextra.commons.action;

import lombok.extern.slf4j.Slf4j;
import applyextra.commons.activity.ActivityException;
import applyextra.commons.model.financialdata.CardType;
import applyextra.commons.orchestration.Action;
import applyextra.commons.util.AccountUtils;
import nl.ing.riaf.ix.serviceclient.ServiceOperationResult;
import nl.ing.riaf.ix.serviceclient.ServiceOperationTask;
import nl.ing.sc.creditcardlegacy.createcreditcardaccount.CreateCreditCardAccountServiceOperationClient;
import nl.ing.sc.creditcardlegacy.createcreditcardaccount.value.CreateCreditCardAccountBusinessRequest;
import nl.ing.sc.creditcardlegacy.types.RepayMethod;
import nl.ing.sc.creditcardmanagement.commonobjects.PortfolioCode;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
@Lazy
public class CreateCreditCardAccountLegacyAction implements Action<CreateCreditCardAccountLegacyAction.CreateCreditCardAccountLegacyDTO, Boolean> {

    @Resource
    private CreateCreditCardAccountServiceOperationClient serviceOperationClient;
    @Resource
    private CreateStudentCreditCardAccountLegacyAction studentAction;

    public Boolean perform(CreateCreditCardAccountLegacyDTO flowDto) {
        // student playingcard is handle by other service operation
        if (CardType.Studentencard.equals(flowDto.getCardType())) {
            return studentAction.perform(flowDto);
        }
        CreateCreditCardAccountBusinessRequest request = mapBusinessRequest(flowDto);

        ServiceOperationTask serviceTask = serviceOperationClient.execute(request);
        ServiceOperationResult serviceResult = serviceTask.getResult();
        if (serviceResult.isOk()) {
            return true;
        } else {
            log.error("Error in DNL_XRRB_CreditCardLegacy_001_CreateCreditCardAccount_001 for requestId " + flowDto.getRequestId());
            throw new ActivityException(CreateCreditCardAccountServiceOperationClient.SERVICE_NAME, Math.abs(serviceResult.getError()
                    .getErrorCode()), // in case it's a negative error code which riaf does not accept
                    "Error in tibco service DNL_XRRB_CreditCardLegacy_001_CreateCreditCardAccount_001 for requestId: " + flowDto.getRequestId(), serviceResult.getError()
                    .getException());
        }
    }

    private static CreateCreditCardAccountBusinessRequest mapBusinessRequest(CreateCreditCardAccountLegacyDTO flowDto) {
        CreateCreditCardAccountBusinessRequest request = new CreateCreditCardAccountBusinessRequest();
        request.setBban(AccountUtils.ibanToBbanAsString(flowDto.getArrangementId()));
        if (CardType.Platinumcard.equals(flowDto.getCardType())){
            request.setCardType(nl.ing.sc.creditcardlegacy.types.CardType.PLATINUMCARD);
        } else { // Both creditcard and studentencard create a creditcard in RRB
            request.setCardType(nl.ing.sc.creditcardlegacy.types.CardType.CREDITCARD);
        }
        if(flowDto.isExceptionFlow()) {
            request.setCreditLimit(flowDto.getRequestedCreditLimit());
        } else {
            request.setCreditLimit(flowDto.getCreditLimit());
        }

        request.setPartyIdMainCardHolder(Integer.parseInt(flowDto.getCustomerId()));
        if (flowDto.getBeneficiaryId() != null) {
            request.setPartyIdBeneficiary(Integer.parseInt(flowDto.getBeneficiaryId()));
        }
        if (PortfolioCode.REVOLVING.equals(flowDto.getPortfolioCode())) {
            request.setRepayMethod(RepayMethod.REVOLVING);
        } else {
            request.setRepayMethod(RepayMethod.CHARGE);
        }
        return request;
    }

    public interface CreateCreditCardAccountLegacyDTO extends CreateStudentCreditCardAccountLegacyAction.CreateStudentCreditCardAccountLegacyDTO {
        String getArrangementId();
        CardType getCardType();
        Integer getCreditLimit();
        String getCustomerId();
        String getBeneficiaryId();
        PortfolioCode getPortfolioCode();
        String getRequestId();
        boolean isExceptionFlow();
        Integer getRequestedCreditLimit();

    }
}
