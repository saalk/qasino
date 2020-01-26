package applyextra.operations.dto;

import lombok.Getter;
import lombok.Setter;
import applyextra.operations.model.SIACreditCard;

import java.util.List;

@Getter
@Setter
public class InquireAccount2DTO {
    private String accountNumber;
    private List<SIACreditCard> cards;
    private String returnCode;
}
