package applyextra.operations.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class SIACreditCardAccount {
    protected String dailyFinancialsResponseItem;
    protected String accountInfoResponseItem;
    protected List<SIACreditCard> siaCreditCard;
    protected String accountNumber;
    protected String accountCategory;
    protected String accountStatus;
    protected String accountStartDate;
    protected String accountType;
    protected String portfolioCode;
    protected String minPaymentPercentage;
    protected String minPaymentAmount;
    protected String creditLineAmount;
    protected String relatedAccountId;
    protected String relatedAccountProduct;
    protected String relatedAccountInfo1;
    protected String relatedAccountInfo2;
    protected String relatedAccountRefId;
}
