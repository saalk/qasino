package applyextra.commons.action;

import applyextra.commons.activity.ActivityException;
import applyextra.commons.event.EventOutput;
import applyextra.commons.orchestration.Action;
import applyextra.commons.util.AccountUtils;
import applyextra.operations.model.PackageType;
import applyextra.operations.model.StatusCode;
import nl.ing.sc.arrangementretrieval.listarrangements2.value.RoleWithArrangement;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * This suppliedMove uses ListArrangements to find package types
 */
@Lazy
@Component
public class DeterminePackageFromListArrangementsResponse implements Action<DeterminePackageFromListArrangementsResponse.DeterminePackageFromListArrangementsResponseDTO, EventOutput> {

    private static final String SERVICE_NAME = "PackageArrangements";

    @Override
    public EventOutput perform(final DeterminePackageFromListArrangementsResponseDTO flowDTO) {
        final List<RoleWithArrangement> arrangements = flowDTO.getRoleWithArrangements();

        if (arrangements == null || arrangements.isEmpty()) {
            throw new ActivityException(SERVICE_NAME, "No package arrangements found for partyId " + flowDTO.getCustomerId(), null);
        }
        final String iban = flowDTO.getArrangementId();

        RoleWithArrangement packageArrangement = arrangements
                .stream() //
                .filter(a -> (StatusCode.ACTIVE.getStatusCode().equals(a.getArrangement().getStatusCode().toString())) // 1 = lopend (Active)
                        && PackageType.isValidPackageType(a)
                        && (AccountUtils.ibanToBbanAsString(iban)
                        .equals(a.getArrangement().getPackageBbanNumber()))).findAny().orElse(null);

        flowDTO.setPackageRefType(
                packageArrangement == null
                        ? null
                        : PackageType.valueOfPackageCode(
                        packageArrangement.getArrangement().getProduct().toString()
                )
        );
        return EventOutput.success();
    }

    public interface DeterminePackageFromListArrangementsResponseDTO {
        List<RoleWithArrangement> getRoleWithArrangements(); // initialize in your DTO

        String getArrangementId();

        void setPackageRefType(PackageType packageRefType);

        PackageType getPackageRefType();

        String getCustomerId();
    }
}

