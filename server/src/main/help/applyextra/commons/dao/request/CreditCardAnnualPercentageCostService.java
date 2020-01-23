package applyextra.commons.dao.request;

import applyextra.commons.model.ProductType;
import applyextra.commons.model.database.entity.CreditCardAnnualPercentageCost;
import applyextra.operations.model.PackageType;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Lazy
@Component
public class CreditCardAnnualPercentageCostService {

    @Resource
    private CreditCardAnnualPercentageCostRepository repository;

    @Transactional(readOnly = true)
    public CreditCardAnnualPercentageCost get(PackageType packageType, ProductType productType, Integer creditAmount) {
        return repository.findByPackageTypeAndProductTypeAndCreditAmount(packageType, productType, creditAmount);
    }

}
