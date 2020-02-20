package applyextra.operations.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeStatusDTO {

    // fill one of these SIA account number or creditcard number
    private String creditcardAccountNumber;
    private String creditcardNumber;

    // always fill
    private String newStatusCode;

    // optionally
    private String newStatusReason;
    private String comment;


}
