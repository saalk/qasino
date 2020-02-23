package applyextra.commons.action;

import applyextra.commons.dao.request.CreditCardAnnualPercentageCostService;
import applyextra.commons.event.EventOutput;
import applyextra.commons.model.ProductType;
import applyextra.commons.model.database.entity.CreditCardAnnualPercentageCost;
import applyextra.commons.orchestration.Action;
import applyextra.operations.model.PackageType;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;

@Component
public class CreditCardAnnualPercentageCostAction implements Action<CreditCardAnnualPercentageCostAction.CreditCardAnnualPercentageCostActionDTO, EventOutput> {

    @Resource
    private CreditCardAnnualPercentageCostService service;

    @Override
    public EventOutput perform(final CreditCardAnnualPercentageCostActionDTO dto) {
        CreditCardAnnualPercentageCost creditCardAnnualPercentageCost = service.get(dto.getPackageType(), dto.getProductType(), dto.getCreditLimit());
        if (creditCardAnnualPercentageCost != null) {
            dto.setJkp(creditCardAnnualPercentageCost.getJkp());
            return EventOutput.success();
        }
        return EventOutput.failure();
    }

    public interface CreditCardAnnualPercentageCostActionDTO {
        PackageType getPackageType();

        ProductType getProductType();

        Integer getCreditLimit();

        void setJkp(BigDecimal interestRate);
    }
}
