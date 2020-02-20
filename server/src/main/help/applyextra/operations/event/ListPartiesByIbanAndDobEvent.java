package applyextra.operations.event;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import applyextra.commons.activity.ActivityException;
import applyextra.commons.event.AbstractEvent;
import applyextra.commons.event.EventOutput;
import nl.ing.riaf.ix.serviceclient.ServiceOperationTask;
import nl.ing.sc.arrangementretrieval.listparties2.ListPartiesServiceOperationClient;
import nl.ing.sc.arrangementretrieval.listparties2.value.*;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
@Lazy
@Slf4j
public class ListPartiesByIbanAndDobEvent extends AbstractEvent {

    private static final String SERVICE_NAME_LIST_PARTIES = "ListParties";

    @Resource
    private ListPartiesServiceOperationClient listPartiesServiceOperationClient;

    @Override
    protected EventOutput execution(final Object... eventInput) {
        ListPartiesEventDTO flowDTO = (ListPartiesEventDTO) eventInput[0];

        final String arrangementValue = flowDTO.getValue();
        final Integer arrangementType = flowDTO.getType();
        final String dateOfBirth = flowDTO.getDateOfBirth() == null
                ? null
                : flowDTO.getDateOfBirth().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        final List<Party> partyList = listParties(arrangementValue, arrangementType);
        final List<SimpleParty> simpleParties = new ArrayList<>();
        /*for (final Party party : partyList) {
            for (final Key key : party.getKeys()) {
                if (party.isIndividual() && Key.TYPE_RGB == key.getType()) {
                    final Individual individual = (Individual) party;
                    if (individual.getBorn().equals(dateOfBirth) || dateOfBirth == null) {
                        final SimpleParty simpleParty = new SimpleParty();
                        simpleParty.setId(key.getId());
                        simpleParty.setName(individual.getName());

                        simpleParties.add(simpleParty);
                    }
                }
            }
        }*/

        partyList.stream().forEach(
                party -> {
                    if (getRgbParty(party) != null && party.isIndividual() && !roleIsEnded(party.getRole())){
                        Individual individual = (Individual) party;
                        if (dateOfBirth == null || dateOfBirth.equals(individual.getBorn())) {
                            SimpleParty simpleParty = new SimpleParty();
                            simpleParty.setId(getRgbParty(party).getId());
                            simpleParty.setName(party.getName());
                            simpleParties.add(simpleParty);
                        }
                    }
                }
        );

        flowDTO.setParties(simpleParties);

        return EventOutput.success();
    }

    private List<Party> listParties(final String arrangementValue, final Integer arrangementType) {

        final ListPartiesBusinessRequest request = new ListPartiesBusinessRequest();
        final List<ArrangementKey> arrangementKeyList = request.getArrangementKeyList();
        final ArrangementKey arrangementKey = new ArrangementKey(arrangementValue, arrangementType);
        arrangementKeyList.add(arrangementKey);

        final ServiceOperationTask<ListPartiesBusinessResponse> response = listPartiesServiceOperationClient.execute(request);
        if (!response.getResult().isOk()) {
            throw new ActivityException(SERVICE_NAME_LIST_PARTIES, response.getResult().getError().getErrorCode(),
                    "Exception returned from list parties", response.getResult().getError().getException());
        } else if(response.getResponse() == null || response.getResponse().getPartyList().isEmpty()) {
            throw new ActivityException(SERVICE_NAME_LIST_PARTIES,
                    "Empty result from list parties", null);
        } else {
            return response.getResponse().getPartyList();
        }
    }

    private Key getRgbParty(Party party) {
        return party.getKeys().stream()
                .filter(key -> Key.TYPE_RGB == key.getType())
                .findAny().orElse(null);
    }

    private boolean roleIsEnded(Role role) {
        return role.getEndDate() != null && role.getEndDate().isBefore(LocalDate.now());
    }

    public interface ListPartiesEventDTO {
        String getValue();
        Integer getType();
        LocalDate getDateOfBirth();
        void setParties(List<SimpleParty> parties);
    }

    @Getter
    @Setter
    public final class SimpleParty {
        String id;
        String name;
    }
}
