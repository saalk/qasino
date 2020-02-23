package applyextra.commons.action;

import nl.ing.api.party.domain.Individual;
import applyextra.api.arrangement.parties.ArrangementPartySearchP2pResourceClient;
import applyextra.api.arrangement.parties.value.ArrangementPartySearchBusinessRequest;
import applyextra.api.arrangement.parties.value.ArrangementPartySearchBusinessResponse;
import applyextra.api.exception.ResourceException;
import applyextra.commons.event.EventOutput;
import applyextra.commons.orchestration.Action;
import nl.ing.riaf.core.util.JNDIUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Lazy
@Component
public class ArrangementPartySearchAction implements Action<ArrangementPartySearchAction.ArrangementPartySearchActionDTO, EventOutput.Result> {
    private static final String CREDITCARDS_TYPE_CODE = "63";

    @Resource private ArrangementPartySearchP2pResourceClient resourceClient;
    @Resource private JNDIUtil jndiUtil;
    private boolean filterDuplicates;

    @PostConstruct
    public void init () {
        filterDuplicates = jndiUtil.getJndiValueWithDefault("param/service/xclasfa/version", false);
    }

    @Override
    public EventOutput.Result perform(ArrangementPartySearchActionDTO  dto) throws RuntimeException {
        ArrangementPartySearchBusinessRequest request = new ArrangementPartySearchBusinessRequest();
        String arrangementIdForPartySearch = dto.getArrangementIdForPartySearch();
        request.setArrangementId(arrangementIdForPartySearch);
        request.setArrangementType(dto.getArrangementTypeForPartySearch());
        try {
            final ArrangementPartySearchBusinessResponse response = resourceClient.execute(request);
            final List<Individual> individuals = filterDuplicates(response.getIndividuals());

            if (individuals.size() == 0) {
                throw new IllegalStateException("No party found for arrangement: " + arrangementIdForPartySearch);
            }
            else if (individuals.size() != 1){
                throw new IllegalStateException(individuals.size() + " parties found for arrangement: " + arrangementIdForPartySearch + ". Expect only 1");
            }
            dto.setCustomerId(individuals.get(0).getPartyId());
            return EventOutput.Result.SUCCESS;
        } catch (ResourceException e) {
                throw new RuntimeException(e);
        }
    }


    /**
     * Logic created due to inability to clean Accp data in cassandra and the data masking process which happens every month.
     * Without this logic we will add 1 additional identical hit on an arrangement each month.
     * @param individuals
     * @return filtered list of individuals based on PartyID.
     */
    private List<Individual> filterDuplicates ( List<Individual> individuals) {
        return !filterDuplicates
            ? individuals
            : individuals.stream().distinct().collect(Collectors.toList());
    }


    public interface ArrangementPartySearchActionDTO {
        //Need to pass P/M in case of a credit playingcard.
        String getArrangementIdForPartySearch();

        default String getArrangementTypeForPartySearch(){
            return CREDITCARDS_TYPE_CODE;
        }

        void setCustomerId(String customerId);
    }
}
