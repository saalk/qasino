package applyextra.commons.action;
import applyextra.commons.event.EventOutput;
import applyextra.commons.orchestration.Action;
import applyextra.operations.model.SimplePaymentAccount;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.ws.rs.WebApplicationException;

@Lazy
@Component
public class GetPackagesFromListAccountsAction implements Action<GetPackagesFromListAccountsAction.PackageAgreementDTO, EventOutput> {
    @Resource
    private PackageArrangementFromListArrangementsAction packageArrangementFromListArrangementsAction;
    @Override
    public EventOutput perform(final PackageAgreementDTO dto) {
        for (SimplePaymentAccount simplePaymentAccount : dto.getListAccountsDTO().getAccountList()) {
            dto.setSimplePaymentAccount(simplePaymentAccount);
            EventOutput eventOutput = packageArrangementFromListArrangementsAction.perform(dto);
            if (dto.getPackageRefType() == null) {
                throw new WebApplicationException("Could not fetch packageType for accountnumber " + simplePaymentAccount.getAccountNumber());
            }
            simplePaymentAccount.setPackageType(dto.getPackageRefType());
        }
        return EventOutput.success();
    }
    public interface PackageAgreementDTO extends ListAccountsAction.ListAccountsActionDTO, PackageArrangementFromListArrangementsAction.PackageArrangementFromListArrangementsActionDTO {
    }
}