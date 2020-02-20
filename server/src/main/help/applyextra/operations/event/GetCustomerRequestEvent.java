package applyextra.operations.event;

import applyextra.commons.activity.ActivityException;
import applyextra.commons.event.AbstractEvent;
import applyextra.commons.event.EventOutput;
import applyextra.operations.dto.CramDTO;
import applyextra.operations.dto.CramStatus;
import nl.ing.riaf.ix.serviceclient.ServiceOperationTask;
import nl.ing.sc.customerrequest.getcustomerrequest1.GetCustomerRequestServiceOperationClient;
import nl.ing.sc.customerrequest.getcustomerrequest1.value.GetCustomerRequestBusinessRequest;
import nl.ing.sc.customerrequest.getcustomerrequest1.value.GetCustomerRequestBusinessResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Lazy
public class GetCustomerRequestEvent extends AbstractEvent {
	@Resource
	private GetCustomerRequestServiceOperationClient serviceClient;

	protected EventOutput execution(Object... eventOutput) {
		CramDTO dto = ((GetCustomerRequestEventDTO) eventOutput[0]).getCramDTO();

		GetCustomerRequestBusinessRequest request = new GetCustomerRequestBusinessRequest();

		request.setReferenceId(dto.getCramId());


		final ServiceOperationTask<GetCustomerRequestBusinessResponse> serviceResult = serviceClient.execute(request);

		if(!serviceResult.getResult().isOk()){
			throw new ActivityException("GetCustomerRequest","Could not execute call to CRAM successfully to obtain the CustomerRequest");
		}

		GetCustomerRequestBusinessResponse response = serviceResult.getResponse();

		dto.setStatus(CramStatus.valueOf(response.getAuthorisationDetails().getStatus().toUpperCase()));

		return new EventOutput(EventOutput.Result.SUCCESS);
	}

	public interface GetCustomerRequestEventDTO {
		CramDTO getCramDTO();
	}
}
