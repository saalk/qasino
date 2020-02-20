package applyextra.operations.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import applyextra.commons.model.Agreement;
import applyextra.commons.model.CreditCard;

/**
 * Created by CL94WQ on 09-11-15.
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreditCardResponse {

	private String requestId;
	private String state;
	private String cramId;
	private CreditCard creditCard;
	private Agreement agreement;
	private String errorCode;
	private String errorReason;
	private String newExistingLimit;
//
//	@SuppressWarnings("rawtypes")
//	private List changeLimitList;
//
//	private List<ChangeLimitSelectionData> changeLimitdata;
//	private String newAssignedCreditLimit;

}
