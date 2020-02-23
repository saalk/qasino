package applyextra.operations.activity;

import lombok.extern.slf4j.Slf4j;
import applyextra.commons.activity.AbstractActivity;
import applyextra.commons.activity.ActivityException;
import applyextra.commons.activity.ActivityOutput;
import applyextra.commons.model.Agreement;
import applyextra.commons.model.CorrespondenceAddress;
import nl.ing.riaf.ix.serviceclient.ErrorResult;
import nl.ing.riaf.ix.serviceclient.ServiceOperationTask;
import nl.ing.sc.arrangementretrieval.listparties2.ListPartiesServiceOperationClient;
import nl.ing.sc.arrangementretrieval.listparties2.value.*;
import nl.ing.sc.individualmanagement.getindividualsummary2.GetIndividualSummaryServiceOperationClient;
import nl.ing.sc.individualmanagement.getindividualsummary2.value.GetIndividualSummaryBusinessRequest;
import nl.ing.sc.individualmanagement.getindividualsummary2.value.GetIndividualSummaryBusinessResponse;
import nl.ing.sc.individualmanagement.value.address.Address;
import nl.ing.sc.individualmanagement.value.address.DomesticAddress;
import nl.ing.sc.individualmanagement.value.address.ForeignAddress;
import nl.ing.sc.individualmanagement.value.address.StreetAddress;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
@Lazy
public class InitializeActivity extends AbstractActivity<Agreement> {

    private static final String SERVICE_NAME_LIST_PARTIES = "ListParties";
    private static final String SERVICE_NAME_GET_INDIVIDUAL_SUMMARY = "GetIndividualSummary";

    private static final Integer CREDIT_CARD_ARRANGEMENT_TYPE = 26;

    private static final Integer PARTY_RGB = 1;

    @Resource
    private ListPartiesServiceOperationClient listPartiesServiceOperationClient;

    @Resource
    private GetIndividualSummaryServiceOperationClient individualManagementServiceClient;

    @Override
    protected ActivityOutput<Agreement> execution(final Object... activityInput) {

        String creditCardNumber = activityInput[0].toString();
        String requestorID = activityInput[1].toString();

        final List<Party> partyList = fetchPartyListFromListArrangements(creditCardNumber);
        final Agreement agreement = new Agreement();
        for (final Party party : partyList) {
            for (final Key key : party.getKeys()) {
                if (party.isIndividual() && Integer.valueOf(Role.OWNER).equals(party.getRole().getType())) {
                    if (PARTY_RGB.equals(key.getType())) {
                        CorrespondenceAddress correspondenceAddress = fetchAddressFromIndividualManagementService(key);
                        agreement.setAccountHolderId(key.getId());
                        agreement.setCorrespondenceAddress(correspondenceAddress);
                        agreement.setArrangementId(key.getId());
                        agreement.setAccountHolderName(correspondenceAddress.getAccountHolderName());
                        if(requestorID.equals(key.getId())){
                            agreement.setRole("HOLDER");
                            agreement.setCardHolderId(key.getId());
                        }
                        else{
                            agreement.setRole("USER");
                            agreement.setCardHolderId(requestorID);
                        }
                    }
                }
            }
        }
        return new ActivityOutput<>(ActivityOutput.Result.SUCCESS, agreement);
    }

    private List<Party> fetchPartyListFromListArrangements(final String creditCardNumber) {

        final ListPartiesBusinessRequest request = new ListPartiesBusinessRequest();
        final List<ArrangementKey> arrangementKeyList = request.getArrangementKeyList();
        final ArrangementKey arrangementKey = new ArrangementKey(creditCardNumber, CREDIT_CARD_ARRANGEMENT_TYPE);
        arrangementKeyList.add(arrangementKey);

        final ServiceOperationTask<ListPartiesBusinessResponse> response = listPartiesServiceOperationClient.execute(request);

        if (response.getResult().isOk() && response.getResponse() != null && !response.getResponse().getPartyList().isEmpty()) {
            return response.getResponse().getPartyList();
        } else {
            return handleResponseError(response);
        }
    }

    private List<Party> handleResponseError(final ServiceOperationTask<ListPartiesBusinessResponse> response) {
        final ErrorResult errorResult = response.getResult().getError();
        if (errorResult == null) {
            throw new ActivityException(SERVICE_NAME_LIST_PARTIES, 0,
                    "Cannot fetch partyList From listPartiesServiceOperationClient", null);
        } else {
            throw new ActivityException(SERVICE_NAME_LIST_PARTIES, errorResult.getErrorCode(),
                    "Cannot fetch partyList From listPartiesServiceOperationClient", errorResult.getException());

        }
    }

    private CorrespondenceAddress fetchAddressFromIndividualManagementService(Key key) {
        Address address = null;
        final GetIndividualSummaryBusinessRequest request = new GetIndividualSummaryBusinessRequest();
        request.setRgb(key.getId());
        final ServiceOperationTask<GetIndividualSummaryBusinessResponse> response = individualManagementServiceClient.execute(request);
        if (!response.getResult().isOk()) {
            Exception e = response.getResult().getError().getException();
            throw new ActivityException(SERVICE_NAME_GET_INDIVIDUAL_SUMMARY, response.getResult().getError().getErrorCode(),
                    "Cannot fetch address from IndividualManagementServiceClient", e);
        } else if (response.getResponse() != null && response.getResponse().getIndividualSummary() != null) {
            address = response.getResponse().getIndividualSummary().getCorrespondenceAddress();
        }
        final CorrespondenceAddress correspondenceAddress = transformAddress(address);
        if (correspondenceAddress.getAddressLine1() == null || correspondenceAddress.getAddressLine2() == null) {
            log.warn("No address information was found. Returning empty address.");
        }
        return correspondenceAddress;
    }

    public static CorrespondenceAddress transformAddress(final Address address) {
        final CorrespondenceAddress correspondenceAddress = new CorrespondenceAddress();
        if (address != null) {
            if (address instanceof ForeignAddress) {
                transformForeignAddress((ForeignAddress) address, correspondenceAddress);
            } else if (address instanceof StreetAddress) {
                transformStreetAddress((StreetAddress) address, correspondenceAddress);
            } else if (address.getClass().equals(DomesticAddress.class)) { // must be a DomesticAddress and not a subclass
                transformDomesticAddress((DomesticAddress) address, correspondenceAddress);
            } else {
                throw new IllegalArgumentException("Unknown address information");
            }
        }
        return correspondenceAddress;
    }

    private static void transformDomesticAddress(final DomesticAddress sourceAddress, final CorrespondenceAddress destAddress) {
        destAddress.setAddressLine1(sourceAddress.getDeliveryInformation());
        destAddress.setAddressLine2(combinePostalCodeAndCity(
                sourceAddress.getPostalCode(),
                sourceAddress.getCity(),
                ""
        ));
    }

    private static void transformStreetAddress(final StreetAddress sourceAddress, final CorrespondenceAddress destAddress) {
        String firstLine = "";
        if (sourceAddress.getStreetName() != null) {
            firstLine += sourceAddress.getStreetName();
        }
        if (sourceAddress.getResidenceNumber() != null) {
            firstLine += " " + sourceAddress.getResidenceNumber();
        }
        if (sourceAddress.getResidenceNumberAddition() != null) {
            firstLine += " " + sourceAddress.getResidenceNumberAddition();
        }
        if (firstLine.isEmpty()) {
            destAddress.setAddressLine1(sourceAddress.getAddressLine1());
        } else {
            destAddress.setAddressLine1(firstLine);
        }

        destAddress.setAddressLine2(combinePostalCodeAndCity(
                sourceAddress.getPostalCode(),
                sourceAddress.getCity(),
                sourceAddress.getAddressLine2()
        ));
    }

    private static String combinePostalCodeAndCity(final String postalCode, final String city, final String defaultLine) {
        String result = "";
        if (postalCode != null) {
            result += postalCode;
        }
        if (city != null) {
            result += " " + city;
        }
        if (result.isEmpty()) {
            result = defaultLine;
        }
        return result;
    }

    private static void transformForeignAddress(final ForeignAddress sourceAddress, final CorrespondenceAddress destAddress) {
        destAddress.setAddressLine1(sourceAddress.getAddressLine1());
        destAddress.setAddressLine2(sourceAddress.getAddressLine2());
        destAddress.setAddressLine3(sourceAddress.getAddressLine3());
    }

}
