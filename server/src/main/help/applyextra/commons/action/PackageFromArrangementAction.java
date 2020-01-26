package applyextra.commons.action;


import lombok.extern.slf4j.Slf4j;
import nl.ing.api.party.domain.Arrangement;
import applyextra.api.parties.arrangement.value.PartyArrangementBusinessRequest;
import applyextra.api.parties.arrangement.value.PartyArrangementBusinessResponse;
import applyextra.commons.activity.ActivityException;
import applyextra.commons.event.EventOutput;
import applyextra.commons.orchestration.Action;
import applyextra.operations.model.PackageType;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Lazy
@Slf4j
@Component
public class PackageFromArrangementAction implements Action<PackageFromArrangementAction.PackageArrangementsFromGArrangementsActionDTO, EventOutput.Result> {

    private static final String ACTIVE_STATUS_CODE = "1";
    private static final String CATEGORY_CODE_PACKAGES = "15";
    private static final String SERVICE_NAME = "gArrangements";

    @Resource
    private GetArrangementsAction getArrangementsAction;

    @Override
    public EventOutput.Result perform(PackageArrangementsFromGArrangementsActionDTO flowDTO) {

        final PartyArrangementBusinessResponse response = getArrangementsAction.perform(flowDTO);

        final String iban = flowDTO.getIban();
        final String productTypeCode = response.getArrangements().stream()
                .filter(a -> isIbanInArrangement(a, iban))
                .findFirst()
                .orElse(new Arrangement())
                .getProductTypeCode();

        final PackageType packageType = PackageType.valueOfPackageCode(productTypeCode);
        if (packageType == null ) { handleMismatchInResult(iban); }

        flowDTO.setPackageRefType(packageType);
        return EventOutput.Result.SUCCESS;
    }

    private boolean isIbanInArrangement (Arrangement arrangement, String iban) {
        return arrangement.getArrangementKeys().stream()
                .anyMatch(arrangementKey -> iban.equals(arrangementKey.getArrangementKeyId()));
    }

    private void handleMismatchInResult (String iban) {
        throw new ActivityException(SERVICE_NAME, "Could not determine packageType for the selected IBAN: " + iban);
    }

    public interface PackageArrangementsFromGArrangementsActionDTO extends GetArrangementsAction.GetArrangementsActionDTO {

        String getIban();
        void setPackageRefType(PackageType packageRefType);

        @Override // Codes required for this action.
        default void addRequestCodesToRequest(PartyArrangementBusinessRequest request) {
            request.getCategoryCodes().add(CATEGORY_CODE_PACKAGES);
            request.getStatusCodes().add(ACTIVE_STATUS_CODE);
        }
    }

}
