package applyextra.commons.action;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ing.api.toolkit.trust.context.ChannelContext;
import lombok.extern.slf4j.Slf4j;
import applyextra.api.exception.ResourceException;
import applyextra.api.parties.individuals.IndividualsAllResourceClient;
import applyextra.api.parties.individuals.IndividualsAllp2pResourceClient;
import applyextra.api.parties.individuals.value.IndividualsAllBusinessRequest;
import applyextra.api.parties.individuals.value.IndividualsAllBusinessResponse;
import applyextra.commons.event.EventOutput;
import applyextra.commons.orchestration.Action;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Slf4j
@Component
public class IndividualsAllForBeneficiaryAction implements Action<IndividualsAllForBeneficiaryAction.IndividualsAllForBeneficiaryActionDto, EventOutput.Result> {

    @Resource
    private IndividualsAllResourceClient resourceClient;
    @Resource
    private IndividualsAllp2pResourceClient p2pResourceClient;

    @Override
    public EventOutput.Result perform(IndividualsAllForBeneficiaryActionDto dto) {
        IndividualsAllBusinessRequest businessRequest = new IndividualsAllBusinessRequest();
        businessRequest.setPartyId(dto.getBeneficiaryId());

        try {
            final IndividualsAllBusinessResponse result;
            log.info("About to call gIndividuals Service Client with customer id --> " + dto.getBeneficiaryId());
            if (dto.getChannelContext() != null) {
                businessRequest.setChannelContext(dto.getChannelContext());
                result = resourceClient.execute(businessRequest);
            } else {
                result = p2pResourceClient.execute(businessRequest);
            }
            log.info("End of gIndividuals Client call");
            dto.setBeneficiaryIndividualsAll(result);

            if (log.isDebugEnabled()) {
                log.debug(new ObjectMapper().writeValueAsString(result));
            }
        } catch (ResourceException e) {
            log.error("Error during gIndividuals Service Client call", e);
            throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
        } catch (JsonProcessingException e) {
            log.debug("Could not write response for debugging for result of IndividualsAll beneficiary: " + dto.getBeneficiaryId());
        }

        return EventOutput.Result.SUCCESS;
    }

    public interface IndividualsAllForBeneficiaryActionDto {
        String getBeneficiaryId();
        ChannelContext getChannelContext();
        void setBeneficiaryIndividualsAll(IndividualsAllBusinessResponse individualsAll);
    }
}
