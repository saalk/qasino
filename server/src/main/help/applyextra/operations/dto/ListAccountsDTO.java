package applyextra.operations.dto;

import lombok.Getter;
import lombok.Setter;
import applyextra.commons.event.AbstractFlowDTO;
import applyextra.operations.model.PaymentAccountWithPackage;
import applyextra.operations.model.SimplePaymentAccount;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ListAccountsDTO extends AbstractFlowDTO{

	private List<SimplePaymentAccount> accountList = new ArrayList<>();
	private List<PaymentAccountWithPackage> extendedAccountList = new ArrayList<>();
	private boolean listAccountResultOk;

}
