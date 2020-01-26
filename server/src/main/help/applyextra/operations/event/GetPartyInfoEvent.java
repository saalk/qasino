package applyextra.operations.event;

import lombok.extern.slf4j.Slf4j;
import applyextra.commons.activity.ActivityException;
import applyextra.commons.event.AbstractEvent;
import applyextra.commons.event.EventOutput;
import applyextra.commons.event.EventOutput.Result;
import applyextra.commons.model.Agreement;
import applyextra.operations.dto.IndividualDetailsDTO;
import nl.ing.riaf.ix.serviceclient.ServiceOperationTask;
import nl.ing.sc.arrangementretrieval.listparties2.ListPartiesServiceOperationClient;
import nl.ing.sc.arrangementretrieval.listparties2.value.*;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
@Lazy
@Slf4j
public class GetPartyInfoEvent extends AbstractEvent {

    private static final String SERVICE_NAME_LIST_PARTIES = "ListParties";
    private static final Integer CREDIT_CARD_ARRANGEMENT_TYPE = 26;
    private static final Integer PARTY_RGB = 1;

    @Resource
    private ListPartiesServiceOperationClient listPartiesServiceOperationClient;
    @Resource
    private GetIndividualSummaryEvent getIndividualSummaryEvent;

    @Override
    protected EventOutput execution(final Object... eventInput) {
        GetPartyInfoEventDTO flowDTO = (GetPartyInfoEventDTO) eventInput[0];

        String creditCardNumber = flowDTO.getCreditCardNumber();
        String partyID = flowDTO.getPartyId();
        ArrangementKey arrangementKey = flowDTO.getArrangementKey();

        final List<Party> partyList;
        if(arrangementKey==null) {
            partyList = fetchPartyListFromListArrangements(creditCardNumber);
        }else {
            partyList = queryListParties(arrangementKey);
        }
        final Agreement agreement = new Agreement();
        for (final Party party : partyList) {
            for (final Key key : party.getKeys()) {
                if (party.isIndividual() && Integer.valueOf(Role.OWNER).equals(party.getRole().getType())) {
                    if (PARTY_RGB.equals(key.getType())) {
                        addCorrespondenceAddressToAgreement(key, agreement);
                        agreement.setAccountHolderId(key.getId());
                        agreement.setArrangementId(key.getId());
                        if (partyID.equals(key.getId())) {
                            agreement.setRole("HOLDER");
                            agreement.setCardHolderId(key.getId());
                        } else {
                            agreement.setRole("USER");
                            agreement.setCardHolderId(partyID);
                        }
                    }
                }
            }
        }
        flowDTO.setAgreement(agreement);
        return new EventOutput(Result.SUCCESS);
    }

    private List<Party> queryListParties(ArrangementKey arrangementKey) {
        final ListPartiesBusinessRequest request = new ListPartiesBusinessRequest();
        request.getArrangementKeyList().add(arrangementKey);
        return queryListParties(request);
    }

    private ListPartiesBusinessRequest createBusinessRequest(String arrangement, Integer arrangementKeyTyep){
        final ListPartiesBusinessRequest request = new ListPartiesBusinessRequest();
        final List<ArrangementKey> arrangementKeyList = request.getArrangementKeyList();
        final ArrangementKey arrangementKey = new ArrangementKey(arrangement, arrangementKeyTyep);
        arrangementKeyList.add(arrangementKey);
        return request;
    }

    private List<Party> queryListParties(final ListPartiesBusinessRequest request){
        final ServiceOperationTask<ListPartiesBusinessResponse> response = listPartiesServiceOperationClient.execute(request);
        if (!response.getResult().isOk()) {
            Exception e = response.getResult().getError().getException();
            throw new ActivityException(SERVICE_NAME_LIST_PARTIES, response.getResult().getError().getErrorCode(),
                    "Exception returned from list parties", e);
        } else if(response.getResponse() == null || response.getResponse().getPartyList().isEmpty()) {
            throw new ActivityException(SERVICE_NAME_LIST_PARTIES,
                    "Empty result from list parties", null);
        } else {
            return response.getResponse().getPartyList();
        }
    }

    private void addCorrespondenceAddressToAgreement(final Key key, final Agreement agreement) {
        final GetIndividualSummaryEvent.GetIndividualSummaryEventDTO eventDTO = new GetIndividualSummaryEvent.GetIndividualSummaryEventDTO() {

            @Override
            public String getPartyId() {
                return key.getId();
            }

            @Override
            public IndividualDetailsDTO getIndividualDetailsDTO() {
                return new IndividualDetailsDTO.Builder().build();
            }

            @Override
            public void setIndividualDetailsDTO(final IndividualDetailsDTO individualDetails) {
                agreement.setCorrespondenceAddress(individualDetails.getCorrespondenceAddress());
                agreement.setAccountHolderName(individualDetails.getCorrespondenceAddress().getAccountHolderName());
            }
        };
        getIndividualSummaryEvent.fireEvent(eventDTO);
    }

    @Deprecated
    private List<Party> fetchPartyListFromListArrangements(final String creditCardNumber) {
        return queryListParties(createBusinessRequest(creditCardNumber, CREDIT_CARD_ARRANGEMENT_TYPE));
    }

    public interface GetPartyInfoEventDTO {
        String getPartyId();

        @Deprecated
        String getCreditCardNumber();

        ArrangementKey getArrangementKey();

        void setAgreement(Agreement agreement);
    }

}
