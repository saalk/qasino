package applyextra.commons.action;

import com.ing.api.toolkit.trust.context.ChannelContext;
import lombok.extern.slf4j.Slf4j;
import applyextra.api.exception.ResourceException;
import applyextra.api.parties.individuals.IndividualsAllResourceClient;
import applyextra.api.parties.individuals.IndividualsAllp2pResourceClient;
import applyextra.api.parties.individuals.value.IndividualsAllBusinessRequest;
import applyextra.api.parties.individuals.value.IndividualsAllBusinessResponse;
import applyextra.commons.event.EventOutput;
import applyextra.commons.orchestration.Action;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Lazy
@Component
public class IndividualsAllAction implements Action<IndividualsAllAction.IndividualsAllActionDTO, EventOutput> {

    @Resource
    private IndividualsAllResourceClient resourceClient;
    @Resource
    private IndividualsAllp2pResourceClient p2pResourceClient;

    @Override
    public EventOutput perform(IndividualsAllActionDTO flowDTO) {
        IndividualsAllBusinessRequest businessRequest = new IndividualsAllBusinessRequest();

        //Set channelcontext as customer if present, otherwise use getCustomerId
        if (flowDTO.getChannelContext() != null) {
            businessRequest.setChannelContext(flowDTO.getChannelContext());
        } else {
            businessRequest.setPartyId(flowDTO.getCustomerId());
        }
        //Get the individual data of the main
        try {
            log.info("About to call gIndividuals Service Client with customer id --> "+flowDTO.getCustomerId());
            if (flowDTO.getChannelContext() != null) {
                flowDTO.setIndividualsAll(resourceClient.execute(businessRequest));
            } else {
                flowDTO.setIndividualsAll(p2pResourceClient.execute(businessRequest));
            }
            log.info("End of gIndividuals Client call");
        } catch (ResourceException e) {
            log.error("Error during gIndividuals Service Client call", e);
            throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
        }

        //now get the individuals data for the beneficiary if present
        if(isNotBlank(flowDTO.getBeneficiaryId())){
            businessRequest.setPartyId(flowDTO.getBeneficiaryId());
			try {
                log.info("About to call gIndividuals Service Client with beneficiary id --> "+flowDTO.getBeneficiaryId());
                if (flowDTO.getChannelContext() != null){
                    flowDTO.setBeneficiaryIndividualsAll(resourceClient.execute(businessRequest));
                } else {
                    flowDTO.setBeneficiaryIndividualsAll(p2pResourceClient.execute(businessRequest));
                }
                log.info("End of gIndividuals Client call for a beneficiary ");
			} catch (ResourceException e) {
                log.error("Error during gIndividuals Service Client call for a beneficiary ", e);
				throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
			}
		}

        return EventOutput.success();
    }

    public interface IndividualsAllActionDTO {
        String getCustomerId();
        ChannelContext getChannelContext();
        void setIndividualsAll(IndividualsAllBusinessResponse response);
        IndividualsAllBusinessResponse getIndividualsAll();
        String getBeneficiaryId();
		void setBeneficiaryIndividualsAll(IndividualsAllBusinessResponse individualsAll);
    }

}
