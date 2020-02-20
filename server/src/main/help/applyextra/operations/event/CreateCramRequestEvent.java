package applyextra.operations.event;

import lombok.extern.slf4j.Slf4j;
import applyextra.commons.activity.ActivityException;
import applyextra.commons.event.AbstractEvent;
import applyextra.commons.event.EventOutput;
import applyextra.operations.dto.CramDTO;
import applyextra.operations.dto.CramStatus;
import nl.ing.riaf.ix.serviceclient.OKResult;
import nl.ing.riaf.ix.serviceclient.ServiceOperationResult;
import nl.ing.riaf.ix.serviceclient.ServiceOperationTask;
import nl.ing.sc.customerrequest.createcustomerrequest1.CreateCustomerRequestServiceOperationClient;
import nl.ing.sc.customerrequest.createcustomerrequest1.value.CreateCustomerRequestBusinessRequest;
import nl.ing.sc.customerrequest.createcustomerrequest1.value.CreateCustomerRequestBusinessResponse;
import nl.ing.sc.customerrequest.value.AuthorisationDetails;
import nl.ing.sc.customerrequest.value.ExternalReference;
import nl.ing.sc.customerrequest.value.Request;
import org.joda.time.LocalDate;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;

@Slf4j
@Component
@Lazy
public class CreateCramRequestEvent extends AbstractEvent {
    private static final String SERVICE_NAME_CREATE_CUSTOMER_REQUEST = "CreateCustomerRequest";
    private static final String AUTHORIZATION_METHOD = "gAuthorizer";
    private static final int EXPIRATION_DAYS = 1;

    @Resource
    private CreateCustomerRequestServiceOperationClient createCustomerRequestServiceOperationClient;

    private static CreateCustomerRequestBusinessRequest businessRequestFillData(final CramDTO dto) {
        final CreateCustomerRequestBusinessRequest businessRequest = new CreateCustomerRequestBusinessRequest();

        Calendar c = Calendar.getInstance();
        Date currentDate = new Date();
        c.setTime(currentDate);
        c.add(Calendar.DATE, EXPIRATION_DAYS);
        currentDate = c.getTime();

        businessRequest.setExternalReference(new ExternalReference(dto.getRequestId(), dto.getOrigin()));
        businessRequest.setPartyId(dto.getPartyId());
        businessRequest.setRequest(new Request(dto.getMdmCode(), dto.getMdmName()));
        businessRequest.setNotificationQueue(dto.getNotificationQueue());
        businessRequest.setRiskLevel(dto.getRiskLevel().getServiceValue());
        businessRequest.setExpirationDate(new LocalDate(currentDate));
        businessRequest.setSMSText(dto.getSmsText());
        businessRequest.setIsDraft(dto.getStatus() == CramStatus.DRAFT);
        businessRequest.getAuthorisations().add(new AuthorisationDetails(dto.getPartyId(), AUTHORIZATION_METHOD));
		businessRequest.setDescription(dto.getDescription());
		
		if( dto.getAuthSubject() != null && dto.getAuthText() != null ){
			businessRequest.setAuthSubject(dto.getAuthSubject());
			businessRequest.setAuthText(dto.getAuthText());	
		}

        return businessRequest;
    }

    @Override
    protected EventOutput execution(Object... objects) {
        CreateCramRequestEventDTO flowDTO = (CreateCramRequestEventDTO) objects[0];
        CramDTO dto = flowDTO.getCramDTO();

        final CreateCustomerRequestBusinessRequest businessRequest = businessRequestFillData(dto);

        ServiceOperationTask taskNew = createCustomerRequestServiceOperationClient.execute(businessRequest);
        ServiceOperationResult result = taskNew.getResult(false);

        if (result.isOk()) {
            OKResult okResult = result.getOk();
            CreateCustomerRequestBusinessResponse response = (CreateCustomerRequestBusinessResponse) okResult.getResponse();

            dto.setCramId(response.getReferenceId());
            dto.setConfirmResult(true);
        } else {
            throw new ActivityException(SERVICE_NAME_CREATE_CUSTOMER_REQUEST,
                    Math.abs(result.getError().getErrorCode()),     // cram returns negative error codes which riaf does not accept
                    "Could not submit request to cram, exception occurred", null);
        }
        return EventOutput.success();
    }

    public interface CreateCramRequestEventDTO {
        CramDTO getCramDTO();
    }
}
