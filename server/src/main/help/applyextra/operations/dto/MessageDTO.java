package applyextra.operations.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Deprecated
public class MessageDTO {
	// Message related field
	private String templateId;

	// Arrangement related field
	private String partyKey;

	// Payload
	private String creditCardNumber;
	private String principalCardHolderName;
	private String ibanNumber;
	private String creditLimit; // this field will be deprecated and
	// creditLimitAmount will be used later
	private Boolean indLimitOverdraft; // limit increase or decrease indicator
	private String accountType; // charge or revolving
	private String productName; // type of playingcard - creditcard or platinumcard
	private Integer packageArrangementType; // Oranje, Royaal, Betaal or Basis
	// Pakket from OMSParty
	private Double creditLimitAmount; // credit limit amount value

	private Double annualDebitInterestRate; // Interest rate for revolving cards
	@Deprecated // use jkp
	private Double annualPercentageRate; // APR (JKP) rate
	private Double remainingCreditAmount; // Remaining credited amount
	private Boolean creditLimitChanged; // Flag to specify if limit has been
	// changed

	private String requestStatus; // IN_PROGRESS | FAILED | FULFILLED
	private String requestTypeId; // GB for ChangeRepayment | LW for ChangeLimit

	private String rejectionCode; // decline reasons for pega; can be used for any reason in future

	private BigDecimal jkp;
}
