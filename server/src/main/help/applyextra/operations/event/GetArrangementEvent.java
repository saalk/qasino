package applyextra.operations.event;

import applyextra.commons.activity.ActivityException;
import applyextra.commons.event.AbstractEvent;
import applyextra.commons.event.EventOutput;
import nl.ing.riaf.ix.serviceclient.OKResult;
import nl.ing.riaf.ix.serviceclient.ServiceOperationResult;
import nl.ing.riaf.ix.serviceclient.ServiceOperationTask;
import nl.ing.sc.dnl_pmdm_arrangementmanagement.getarrangement1.GetArrangementServiceOperationClient;
import nl.ing.sc.dnl_pmdm_arrangementmanagement.getarrangement1.value.Arrangement;
import nl.ing.sc.dnl_pmdm_arrangementmanagement.getarrangement1.value.GetArrangementBusinessRequest;
import nl.ing.sc.dnl_pmdm_arrangementmanagement.getarrangement1.value.GetArrangementBusinessResponse;
import nl.ing.sc.dnl_pmdm_arrangementmanagement.model.ArrangementKey;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * Get arrangement information from MDM
 *
 * This event supports 2 EventDTO's:
 * - GetCreditCardArrangementEventDTO: Retrieve the arrangement based on creditcard number
 * - GetArrangementEventDTO: Retrieve the arrangement based on a list of arrangement keys
 *
 * Beware: This event might return null for arrangement
 *
 */
@Component
@Lazy
public class GetArrangementEvent extends AbstractEvent {

    private static final String SERVICE_NAME = "GetArrangement";
    private static final int CREDIT_CARD_ARRANGEMENT_TYPE = 26;

    @Resource
    private GetArrangementServiceOperationClient serviceClient;

    @Override
    protected EventOutput execution(final Object... input) {
        final GetArrangementEventDTO flowDTO;
        if (input[0] instanceof GetCreditCardArrangementEventDTO) {
            flowDTO = createCreditCardDTO((GetCreditCardArrangementEventDTO) input[0]);
        } else {
            flowDTO = (GetArrangementEventDTO) input[0];
        }

        final GetArrangementBusinessRequest businessRequest = new GetArrangementBusinessRequest();
        businessRequest.getAgreementArrangementKeys().addAll(flowDTO.getArrangementKeys());


        ServiceOperationTask taskNew = serviceClient.execute(businessRequest);
        ServiceOperationResult result = taskNew.getResult(false);

        if (result.isOk()) {
            OKResult okResult = result.getOk();
            GetArrangementBusinessResponse response = (GetArrangementBusinessResponse) okResult.getResponse();

            if (response.getArrangement() == null) {
                flowDTO.setArrangement(new Arrangement(null, null, null, null, null, null));
            } else {
                flowDTO.setArrangement(response.getArrangement());
            }
        } else {
            throw new ActivityException(SERVICE_NAME,
                    Math.abs(result.getError().getErrorCode()),     // cram returns negative error codes which riaf does not accept
                    "Could not collect arrangement information, exception occurred", null);
        }

        return EventOutput.success();
    }

    private GetArrangementEventDTO createCreditCardDTO(final GetCreditCardArrangementEventDTO flowDTO) {
        return new GetArrangementEventDTO() {
            @Override
            public List<ArrangementKey> getArrangementKeys() {
                return Collections.singletonList(new ArrangementKey(flowDTO.getCreditCardNumber(), CREDIT_CARD_ARRANGEMENT_TYPE));
            }

            @Override
            public void setArrangement(final Arrangement arrangement) {
                flowDTO.setArrangement(arrangement);
            }
        };
    }

    public interface GetCreditCardArrangementEventDTO {
        String getCreditCardNumber();

        void setArrangement(Arrangement arrangement);
    }

    public interface GetArrangementEventDTO {
        List<ArrangementKey> getArrangementKeys();

        void setArrangement(Arrangement arrangement);
    }

}
