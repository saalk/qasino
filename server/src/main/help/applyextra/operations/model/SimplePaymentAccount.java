package applyextra.operations.model;

import lombok.Getter;
import lombok.Setter;
import applyextra.api.currentaccounts.value.PaymentAccount;

@Setter
@Getter
public class SimplePaymentAccount {
	public SimplePaymentAccount(PaymentAccount paymentAccount, String encryptedAccountNumber){
		this.setId(encryptedAccountNumber);
		this.setName(paymentAccount.getName());
		this.setBalance(paymentAccount.getBalance());
		this.setAccountNumber(paymentAccount.getAccountNumber());
		this.setProductType(paymentAccount.getProductType());
	}
	public SimplePaymentAccount(PaymentAccount paymentAccount, String encryptedAccountNumber, PackageType packageType){
		this(paymentAccount, encryptedAccountNumber);
		this.setPackageType(packageType);
	}

	public SimplePaymentAccount() {}

    private String id;
    private String balance;
    private String name;
    private Integer productType;
    private String accountNumber;
	private PackageType packageType;

}
