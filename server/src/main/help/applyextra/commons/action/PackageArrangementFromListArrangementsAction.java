package applyextra.commons.action;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import applyextra.commons.activity.ActivityException;
import applyextra.commons.event.EventOutput;
import applyextra.commons.model.financialdata.CardType;
import applyextra.commons.orchestration.Action;
import applyextra.commons.util.AccountUtils;
import applyextra.operations.model.PackageType;
import applyextra.operations.model.SimplePaymentAccount;
import applyextra.operations.model.StatusCode;
import nl.ing.riaf.ix.serviceclient.ServiceOperationTask;
import nl.ing.sc.arrangementretrieval.listarrangements2.ListArrangementsServiceOperationClient;
import nl.ing.sc.arrangementretrieval.listarrangements2.value.ListArrangementsBusinessRequest;
import nl.ing.sc.arrangementretrieval.listarrangements2.value.ListArrangementsBusinessResponse;
import nl.ing.sc.arrangementretrieval.listarrangements2.value.RoleWithArrangement;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * This suppliedMove uses ListArrangements to find package types
 */
@Lazy
@Component
public class PackageArrangementFromListArrangementsAction implements Action<PackageArrangementFromListArrangementsAction.PackageArrangementFromListArrangementsActionDTO, EventOutput> {

    private static final String SERVICE_NAME = "PackageArrangements";
    private static final Integer CATEGORY_CODE_PACKAGES = 15;

    @Resource
    private ListArrangementsServiceOperationClient listArrangementsServiceOperationClient;

    @Override
    public EventOutput perform(final PackageArrangementFromListArrangementsActionDTO flowDTO) {
        List<RoleWithArrangement> arrangements = new ArrayList<>();

        final ListArrangementsBusinessRequest request = new ListArrangementsBusinessRequest(flowDTO.getCustomerId(),
                ListArrangementsBusinessRequest.PartyType.PRIVATE);
        request.getCategoryCodes().add(CATEGORY_CODE_PACKAGES);


        final ServiceOperationTask<ListArrangementsBusinessResponse> response = listArrangementsServiceOperationClient
                .execute(request);
        if (response.getResult().isOk()) {
            arrangements.addAll(response.getResponse()
                    .getRoleWithArrangementList());

        } else {
            throw new ActivityException(SERVICE_NAME, response.getResult()
                    .getError()
                    .getErrorCode(), "Cannot fetch role list from list arrangements", null);
        }

        if (arrangements.isEmpty()) {
            throw new ActivityException(SERVICE_NAME, "No package arrangements found for partyId " + flowDTO.getCustomerId(), null);
        }
        final String iban;
        if (isNotBlank(flowDTO.getArrangementId())) {
            iban = flowDTO.getArrangementId();
        } else {
            iban = flowDTO.getSimplePaymentAccount().getAccountNumber();
        }
        RoleWithArrangement packageArrangement = arrangements.stream() //
                .filter(a -> (StatusCode.ACTIVE.getStatusCode().equals(a.getArrangement().getStatusCode().toString())) // 1 = lopend (Active)
                        && (a.getArrangement().getProduct().equals(143) // OranjePakket
                        || a.getArrangement().getProduct().equals(142) // BasisPakket
                        || a.getArrangement().getProduct().equals(141) // RoyaalPakket
                        || a.getArrangement().getProduct().equals(140)) // BetaalPakket
                        && (AccountUtils.ibanToBbanAsString(iban)
                        .equals(a.getArrangement().getPackageBbanNumber()))).findAny().orElse(null);
        if (packageArrangement != null) {
            switch (packageArrangement.getArrangement().getProduct()) {
                case (140):
                    flowDTO.setPackageRefType(PackageType.BETAALPAKKET);
                    break;
                case (141):
                    flowDTO.setPackageRefType(PackageType.ROYAALPAKKET);
                    break;
                case (142):
                    flowDTO.setPackageRefType(PackageType.BASISPAKKET);
                    break;
                case (143):
                    flowDTO.setPackageRefType(PackageType.ORANJEPAKKET);
            }
        }
        return EventOutput.success();
    }

    public interface PackageArrangementFromListArrangementsActionDTO {

        List<RoleWithArrangement> getRoleWithArrangements();

        String getArrangementId();

        void setPackageRefType(PackageType packageRefType);

        PackageType getPackageRefType();

        SimplePaymentAccount getSimplePaymentAccount();

        void setSimplePaymentAccount(SimplePaymentAccount simplePaymentAccount);

        String getCustomerId();
    }

    @Getter
    @Setter
    @ToString
    public final class PackageInfo {
        PackageType refType;
        CardType cardType;
    }
}
