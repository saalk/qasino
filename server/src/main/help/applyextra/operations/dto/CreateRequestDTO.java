package applyextra.operations.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
public class CreateRequestDTO {

	
	private Integer requestType;
	private Integer channelTypeCode;
	private String customerId;
	private Integer accountHolder;
	private Integer cardHolder;
	private String arrangement;
	private String subArrangement;
	private BigInteger cardNumber;
	private String accountNumber;
	private String authenticationId;
	private String requestId;
	
}
