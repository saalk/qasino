package applyextra.operations.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PaymentAccountWithPackage extends SimplePaymentAccount{
    private PackageType packageType;
    public PaymentAccountWithPackage(SimplePaymentAccount simplePaymentAccount, PackageType packageType){
        this.setAccountNumber(simplePaymentAccount.getAccountNumber());
        this.setId(simplePaymentAccount.getId());
        this.setBalance(simplePaymentAccount.getBalance());
        this.setProductType(simplePaymentAccount.getProductType());
        this.setName(simplePaymentAccount.getName());
        this.setPackageType(packageType);
    }
}
