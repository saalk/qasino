package applyextra.commons.action;

import lombok.extern.slf4j.Slf4j;
import applyextra.commons.activity.ActivityException;
import applyextra.commons.orchestration.Action;
import applyextra.commons.util.AccountUtils;
import nl.ing.riaf.ix.serviceclient.ServiceOperationResult;
import nl.ing.riaf.ix.serviceclient.ServiceOperationTask;
import nl.ing.sc.creditcardlegacy.createstudentcreditcardaccount.CreateStudentCreditCardAccountServiceOperationClient;
import nl.ing.sc.creditcardlegacy.createstudentcreditcardaccount.value.CreateStudentCreditCardAccountBusinessRequest;
import nl.ing.sc.creditcardlegacy.types.CardType;
import nl.ing.sc.creditcardlegacy.types.RepayMethod;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
@Lazy
public class CreateStudentCreditCardAccountLegacyAction implements Action<CreateStudentCreditCardAccountLegacyAction.CreateStudentCreditCardAccountLegacyDTO, Boolean> {

    @Resource
    private CreateStudentCreditCardAccountServiceOperationClient serviceOperationClient;

    public Boolean perform(CreateStudentCreditCardAccountLegacyDTO flowDto) {
        CreateStudentCreditCardAccountBusinessRequest request = mapBusinessRequest(flowDto);

        ServiceOperationTask serviceTask = serviceOperationClient.execute(request);
        ServiceOperationResult serviceResult = serviceTask.getResult();
        if (serviceResult.isOk()) {
            return true;
        } else {
            log.error("Error in DNL_XRRB_CreditCardLegacy_001_CreateStudentCreditCardAccount_001 for requestId " + flowDto.getRequestId());
            throw new ActivityException(CreateStudentCreditCardAccountServiceOperationClient.SERVICE_NAME, Math.abs(serviceResult.getError().getErrorCode()), // in case it's a negative error code which riaf does not accept
                    "Error in tibco service DNL_XRRB_CreditCardLegacy_001_CreateStudentCreditCardAccount_001 for requestId: " + flowDto.getRequestId(), serviceResult.getError().getException());
        }
    }

    private static CreateStudentCreditCardAccountBusinessRequest mapBusinessRequest(CreateStudentCreditCardAccountLegacyDTO flowDto) {
        CreateStudentCreditCardAccountBusinessRequest request = new CreateStudentCreditCardAccountBusinessRequest();
        request.setBban(AccountUtils.ibanToBbanAsString(flowDto.getArrangementId()));
        request.setCardType(CardType.STUDENTCARD);
        if(flowDto.isExceptionFlow()) {
            request.setCreditLimit(flowDto.getRequestedCreditLimit());
        }else {
            request.setCreditLimit(flowDto.getCreditLimit());
        }

        request.setPartyIdMainCardHolder(Integer.parseInt(flowDto.getCustomerId()));
        request.setRepayMethod(RepayMethod.CHARGE);
        return request;
    }

    public interface CreateStudentCreditCardAccountLegacyDTO {
        String getArrangementId();

        Integer getCreditLimit();

        String getCustomerId();

        String getRequestId();

        boolean isExceptionFlow();

        Integer getRequestedCreditLimit();
    }
}
