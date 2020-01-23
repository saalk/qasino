package applyextra.operations.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class SIACreditCard {
    protected String cardNumber;
    protected String cardStatus;
    protected String plasticType;
    protected Date expiryDate;
    protected String embossingLine1;
    protected String embossingLine2;
    protected Date embossingDate;
    protected String activationFlag;
    protected Date activationDate;
}
