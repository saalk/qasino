package applyextra.operations.event;

import applyextra.commons.activity.ActivityException;
import applyextra.commons.event.AbstractEvent;
import applyextra.commons.event.EventOutput;
import applyextra.commons.event.EventOutput.Result;
import nl.ing.riaf.ix.serviceclient.ServiceOperationTask;
import nl.ing.sc.arrangementretrieval.listarrangements2.ListArrangementsServiceOperationClient;
import nl.ing.sc.arrangementretrieval.listarrangements2.value.ListArrangementsBusinessRequest;
import nl.ing.sc.arrangementretrieval.listarrangements2.value.ListArrangementsBusinessResponse;
import nl.ing.sc.arrangementretrieval.listarrangements2.value.RoleWithArrangement;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
@Lazy
public class GetListArrangementsEvent extends AbstractEvent {

    private static final String SERVICE_NAME = "ListArrangements";

    @Resource
    private ListArrangementsServiceOperationClient listArrangementsServiceOperationClient;

    @Override
    protected EventOutput execution(final Object... eventInput) {
        final GetListArrangementsEventDTO flowDTO = (GetListArrangementsEventDTO) eventInput[0];

        final String partyID = flowDTO.getCustomerId();

        final ListArrangementsBusinessRequest request = new ListArrangementsBusinessRequest(partyID,
                ListArrangementsBusinessRequest.PartyType.PRIVATE,
                null);
        final ServiceOperationTask<ListArrangementsBusinessResponse> response = listArrangementsServiceOperationClient
                .execute(request);
        if (response.getResult().isOk()) {
            flowDTO.getRoleWithArrangements()
                    .addAll(response.getResponse()
                            .getRoleWithArrangementList());
        } else if(response.getResponse() == null || response.getResponse().getRoleWithArrangementList().isEmpty()) {
            throw new ActivityException(SERVICE_NAME, "Cannot fetch role list from list arrangements", null);
        } else {
            throw new ActivityException(SERVICE_NAME, response.getResult()
                    .getError()
                    .getErrorCode(), "Cannot fetch role list from list arrangements", null);
        }

        return new EventOutput(Result.SUCCESS);
    }

    public interface GetListArrangementsEventDTO {
        String getCustomerId();
        List<RoleWithArrangement> getRoleWithArrangements();
    }

}
