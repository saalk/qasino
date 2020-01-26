package applyextra.operations.activity;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import applyextra.commons.activity.AbstractActivity;
import applyextra.commons.activity.ActivityException;
import applyextra.commons.activity.ActivityOutput;
import applyextra.operations.dto.CramDTO;
import nl.ing.riaf.core.exception.RIAFRuntimeException;
import nl.ing.riaf.ix.serviceclient.OKResult;
import nl.ing.riaf.ix.serviceclient.ServiceOperationErrorType;
import nl.ing.riaf.ix.serviceclient.ServiceOperationResult;
import nl.ing.riaf.ix.serviceclient.ServiceOperationTask;
import nl.ing.sc.customerrequest.setcustomerrequeststatus1.SetCustomerRequestStatusServiceOperationClient;
import nl.ing.sc.customerrequest.setcustomerrequeststatus1.value.SetCustomerRequestStatusBusinessRequest;
import nl.ing.sc.customerrequest.setcustomerrequeststatus1.value.SetCustomerRequestStatusBusinessResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
@Lazy
public class SetCramStatusActivity extends AbstractActivity<CramDTO> {
    private static final String SERVICE_NAME_SET_CUSTOMER_REQUEST = "SetCustomerRequestStatus";

    public enum CramStatus {
        DRAFT("Draft"),
        SUBMITTED("Submitted"),
        AUTHORIZED("Authorized"),
        EXECUTION("Execution"),
        FULFILLED("Fulfilled"),
        DECLINED("Declined"),
        EXPIRED("Expired"),
        CANCELLED("Cancelled");

        @Getter
        private final String statusString;
        CramStatus(String statusString) {
            this.statusString = statusString;
        }
    }

    @Resource
    private SetCustomerRequestStatusServiceOperationClient setCustomerRequestStatusServiceOperationClient;

    @Override
    protected ActivityOutput<CramDTO> execution(Object... objects) {
        CramDTO dto = (CramDTO) objects[0];

        final SetCustomerRequestStatusBusinessRequest businessRequest = new SetCustomerRequestStatusBusinessRequest();
        SetCustomerRequestStatusBusinessResponse businessResponse;

        businessRequest.setReferenceId(dto.getCramId());
        businessRequest.setStatusType(dto.getStatus().getStatusString());

        ServiceOperationTask taskNew = setCustomerRequestStatusServiceOperationClient.execute(businessRequest);
        ServiceOperationResult result = taskNew.getResult(false);

        if (result.isOk()) {
            OKResult okSetResult = result.getOk();
            businessResponse = (SetCustomerRequestStatusBusinessResponse) okSetResult.getResponse();

            dto.setCramId(businessResponse.getReferenceId());
        } else if(ServiceOperationErrorType.TIMEOUT.equals(result.getError().getErrorType())){
            log.error(result.getError().getException().getMessage());
            throw new RIAFRuntimeException(result.getError().getException().getMessage());
        } else {
            throw new ActivityException(SERVICE_NAME_SET_CUSTOMER_REQUEST,
                    Math.abs(result.getError().getErrorCode()),     // cram returns negative error codes which riaf does not accept
                    "Could not update the status in cram, exception occurred", null);
        }
        return new ActivityOutput<>(ActivityOutput.Result.SUCCESS, dto);
    }
}
