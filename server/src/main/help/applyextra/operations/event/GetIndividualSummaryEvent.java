package applyextra.operations.event;

import lombok.extern.slf4j.Slf4j;
import applyextra.commons.activity.ActivityException;
import applyextra.commons.event.AbstractEvent;
import applyextra.commons.event.EventOutput;
import applyextra.commons.model.CorrespondenceAddress;
import applyextra.operations.dto.IndividualDetailsDTO;
import nl.ing.riaf.ix.serviceclient.ServiceOperationTask;
import nl.ing.sc.individualmanagement.getindividualsummary2.GetIndividualSummaryServiceOperationClient;
import nl.ing.sc.individualmanagement.getindividualsummary2.value.GetIndividualSummaryBusinessRequest;
import nl.ing.sc.individualmanagement.getindividualsummary2.value.GetIndividualSummaryBusinessResponse;
import nl.ing.sc.individualmanagement.getindividualsummary2.value.IndividualSummary;
import nl.ing.sc.individualmanagement.value.address.Address;
import nl.ing.sc.individualmanagement.value.address.DomesticAddress;
import nl.ing.sc.individualmanagement.value.address.ForeignAddress;
import nl.ing.sc.individualmanagement.value.address.StreetAddress;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
/**
 * This activity retrieves the following information for an individual:
 *    - correspondenceName
 *    - birth date
 *    - citizenCountry
 *    - gender
 *    - phoneNumber
 *    - residentialAddress
 *    - correspondenceAddress
 */
@Lazy
@Deprecated
public class GetIndividualSummaryEvent extends AbstractEvent {
    private static final String SERVICE_NAME_GET_INDIVIDUAL_SUMMARY = "GetIndividualSummary";

    @Resource
    private GetIndividualSummaryServiceOperationClient serviceClient;

    /**
     * Perform the actual retrieval of the information from the service client
     *
     * @param eventInput First parameter must contain the partyId of the selected individual
     * @return Result of the event with the retrieved data, when the call has been successful
     * @throws ActivityException when the call to the service client failed or if no data was retrieved
     */
    @Override
    protected EventOutput execution(final Object... eventInput) {
        final GetIndividualSummaryEventDTO flowDTO = (GetIndividualSummaryEventDTO) eventInput[0];
        final GetIndividualSummaryBusinessRequest request = new GetIndividualSummaryBusinessRequest();
        request.setRgb(flowDTO.getPartyId());

        final ServiceOperationTask<GetIndividualSummaryBusinessResponse> response = serviceClient.execute(request);

        if (response.getResult().isOk() && response.getResponse() != null && response.getResponse().getIndividualSummary() != null) {
            final IndividualDetailsDTO.Builder resultData = new IndividualDetailsDTO.Builder().withId(flowDTO.getPartyId());
            fillResultWithSource(response.getResponse().getIndividualSummary(), resultData);
            flowDTO.setIndividualDetailsDTO(resultData.build());
            return EventOutput.success();
        } else {
            throw new ActivityException(SERVICE_NAME_GET_INDIVIDUAL_SUMMARY, response.getResult().getError().getErrorCode(),
                    "Cannot fetch information from IndividualManagementServiceClient", null);
        }
    }

    private void fillResultWithSource(final IndividualSummary source, final IndividualDetailsDTO.Builder dest) {
        dest.withBornDate(source.getBornDate())
                .withCitizenCountry(source.getCitizenCountry())
                .withCorrespondenceAddress(transformAddress(source.getCorrespondenceAddress()))
                .withCorrespondenceName(source.getCorrespondenceName())
                .withGender(source.getGender())
                .withPhoneNumber(source.getPhoneNumber())
                .withResidentialAddress(source.getResidentialAddress());
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

    public interface GetIndividualSummaryEventDTO {
        String getPartyId();
        IndividualDetailsDTO getIndividualDetailsDTO();
        void setIndividualDetailsDTO(IndividualDetailsDTO individualDetails);
    }

}
