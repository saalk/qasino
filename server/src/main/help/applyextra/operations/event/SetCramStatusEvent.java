package applyextra.operations.event;

import lombok.extern.slf4j.Slf4j;
import applyextra.commons.activity.ActivityException;
import applyextra.commons.event.AbstractEvent;
import applyextra.commons.event.EventOutput;
import applyextra.operations.dto.CramDTO;
import applyextra.operations.dto.CramStatus;
import nl.ing.riaf.core.exception.RIAFRuntimeException;
import nl.ing.riaf.ix.serviceclient.ServiceOperationErrorType;
import nl.ing.riaf.ix.serviceclient.ServiceOperationResult;
import nl.ing.riaf.ix.serviceclient.ServiceOperationTask;
import nl.ing.sc.customerrequest.setcustomerrequeststatus1.SetCustomerRequestStatusServiceOperationClient;
import nl.ing.sc.customerrequest.setcustomerrequeststatus1.value.SetCustomerRequestStatusBusinessRequest;
import nl.ing.sc.customerrequest.setcustomerrequeststatus1.value.SetCustomerRequestStatusBusinessResponse;
import nl.ing.sc.customerrequest.updatecustomerrequest1.UpdateCustomerRequestServiceOperationClient;
import nl.ing.sc.customerrequest.updatecustomerrequest1.value.UpdateCustomerRequestBusinessRequest;
import nl.ing.sc.customerrequest.updatecustomerrequest1.value.UpdateCustomerRequestBusinessResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static applyextra.commons.state.CreditCardsStateMachine.Trigger;

@Component
@Slf4j
@Lazy
public class SetCramStatusEvent extends AbstractEvent {

    private static final String SERVICE_NAME_UPDATE_CUSTOMER_REQUEST = "UpdateCustomerRequest";
    private static final String SERVICE_NAME_SET_CUSTOMER_REQUEST = "SetCustomerRequestStatus";

    @Resource
    private UpdateCustomerRequestServiceOperationClient updateCustomerRequestServiceOperationClient;
    @Resource
    private SetCustomerRequestStatusServiceOperationClient setCustomerRequestStatusServiceOperationClient;

    @Override
    protected EventOutput execution(Object... objects) {
        SetCramStatusEventDTO flowDTO = (SetCramStatusEventDTO) objects[0];
        CramDTO dto = flowDTO.getCramDTO();
        if (dto.getStatus() == CramStatus.DRAFT) {
            return setCramToUpdate(dto);
        } else {
            return setCramStatus(dto);
        }
    }

    private EventOutput setCramToUpdate(final CramDTO dto) {
        final UpdateCustomerRequestBusinessRequest businessRequest = new UpdateCustomerRequestBusinessRequest();

        businessRequest.setReferenceId(dto.getCramId());
        businessRequest.setIsDraft(true);

        ServiceOperationTask<UpdateCustomerRequestBusinessResponse> taskNew = updateCustomerRequestServiceOperationClient.execute(businessRequest);
        ServiceOperationResult result = taskNew.getResult(false);
        handleResult(result, SERVICE_NAME_UPDATE_CUSTOMER_REQUEST);

        UpdateCustomerRequestBusinessResponse businessResponse = (UpdateCustomerRequestBusinessResponse) result.getOk().getResponse();
        dto.setCramId(businessResponse.getReferenceId());
        dto.setConfirmResult(true);
        return new EventOutput(EventOutput.Result.SUCCESS, Trigger.RESET);
    }

    private EventOutput setCramStatus(final CramDTO dto) {
        final SetCustomerRequestStatusBusinessRequest businessRequest = new SetCustomerRequestStatusBusinessRequest();
        SetCustomerRequestStatusBusinessResponse businessResponse;

        businessRequest.setReferenceId(dto.getCramId());
        businessRequest.setStatusType(dto.getStatus().getStatusString());

        ServiceOperationTask taskNew = setCustomerRequestStatusServiceOperationClient.execute(businessRequest);
        ServiceOperationResult result = taskNew.getResult(false);
        handleResult(result, SERVICE_NAME_SET_CUSTOMER_REQUEST);

        businessResponse = (SetCustomerRequestStatusBusinessResponse) result.getOk().getResponse();
        dto.setCramId(businessResponse.getReferenceId());
        return new EventOutput(EventOutput.Result.SUCCESS);
    }

    private void handleResult(final ServiceOperationResult result, final String serviceName) {
        if (result.isOk()) {
            // No exception needs to be thrown
            return;
        } else if (ServiceOperationErrorType.TIMEOUT.equals(result.getError().getErrorType())) {
            log.error(result.getError().getException().getMessage());
            throw new RIAFRuntimeException(result.getError().getException().getMessage());
        } else {
            throw new ActivityException(serviceName,
                    Math.abs(result.getError().getErrorCode()),     // cram returns negative error codes which riaf does not accept
                    "Could not update the status in cram, exception occurred", null);
        }
    }

    public interface SetCramStatusEventDTO {
        CramDTO getCramDTO();
    }

}
