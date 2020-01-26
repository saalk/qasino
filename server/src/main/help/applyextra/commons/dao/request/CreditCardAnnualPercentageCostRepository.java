package applyextra.commons.dao.request;


import applyextra.commons.model.ProductType;
import applyextra.commons.model.database.entity.CreditCardAnnualPercentageCost;
import applyextra.operations.model.PackageType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreditCardAnnualPercentageCostRepository extends JpaRepository<CreditCardAnnualPercentageCost, Integer> {

    CreditCardAnnualPercentageCost findByPackageTypeAndProductTypeAndCreditAmount(PackageType packageType, ProductType productType, Integer creditAmount);
}
