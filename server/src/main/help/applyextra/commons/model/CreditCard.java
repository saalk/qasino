package applyextra.commons.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreditCard {

	private String name;
	@JsonIgnore
	private String creditCardNumber;

	@JsonProperty("cardId")
	@NotNull(message = "{mandatory.cardId}")
	@Pattern(regexp = "[0-9]+", message = "{numberFormat.cardId}")
	private String id;

	private String maskedCreditcardNumber;


	private String accountNumber;

	private String type;

	private String productType;

	private String ibanNumber;

	private String ibanPrefix;

	private String startDate;
	@JsonIgnore
	private String expirationDate;
	@JsonIgnore
	private String arrangementNumber;
	@JsonIgnore
	private String subArrangementNumber;

	private String status;

	private String statusReason;

	private String currentAccount;

	private String assignedCreditLimit;

    @JsonIgnore
	private String portfolioCode;
	private String balanceAmount;

	@Pattern(regexp = "[0-9]+", message = "{numberFormat.cardId}")
	private String linkedMainCardId;

}
