package applyextra.commons.model.payload;

import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Payload class
 */
@NoArgsConstructor
@XmlRootElement(name = "DocumentData")
public class DocumentData {

    @XmlElement(name = "CreditCardNumber")
    protected String creditCardNumber = "";
    @XmlElement(name = "PrincipalCardHolderName")
    protected String principalCardHolderName = "";
    @XmlElement(name = "CodeDeliveryPeriod")
    protected String codeDeliveryPeriod = "";
    @XmlElement(name = "CreditLimitAmount")
    protected String creditLimit = "";
    @XmlElement(name = "ReferencedAccountNumber")
    protected String ibanNumber = "";
    @XmlElement(name = "AnnualDebitInterestRate")
    protected String annualDebitInterestRate = "";
    @XmlElement(name = "AnnualCostRate")
    protected String annualCostRate = "";
    @XmlElement(name = "RemainingCreditAmount")
    protected String remainingCreditAmount = "";
    @XmlElement(name = "CreditLimitChanged")
    protected String creditLimitChanged = "";
    @XmlElement(name = "ProductName")
    protected String productName = "";
    @XmlElement(name = "RequestStatus")
    protected String requestStatus;
    @XmlElement(name = "RequestTypeID")
    protected String requestTypeId;

    public void setCreditCardNumber(final String creditCardNumber) {
        if (creditCardNumber == null) {
            this.creditCardNumber = "";
        } else {
            this.creditCardNumber = creditCardNumber;
        }
    }

    public void setPrincipalCardHolderName(final String principalCardHolderName) {
        if (principalCardHolderName == null) {
            this.principalCardHolderName = "";
        } else {
            this.principalCardHolderName = principalCardHolderName;
        }
    }

    public void setCodeDeliveryPeriod(final String codeDeliveryPeriod) {
        if (codeDeliveryPeriod == null) {
            this.codeDeliveryPeriod = "";
        } else {
            this.codeDeliveryPeriod = codeDeliveryPeriod;
        }
    }

    public void setCreditLimit(final String creditLimit) {
        if (creditLimit == null) {
            this.creditLimit = "";
        } else {
            this.creditLimit = creditLimit;
        }
    }

    public void setIbanNumber(final String ibanNumber) {
        if (ibanNumber == null) {
            this.ibanNumber = "";
        } else {
            this.ibanNumber = ibanNumber;
        }
    }

    public void setAnnualDebitInterestRate(final String annualDebitInterestRate) {
        if (annualDebitInterestRate == null) {
            this.annualDebitInterestRate = "";
        } else {
            this.annualDebitInterestRate = annualDebitInterestRate;
        }
    }

    public void setAnnualCostRate(final String annualCostRate) {
        if (annualCostRate == null) {
            this.annualCostRate = "";
        } else {
            this.annualCostRate = annualCostRate;
        }
    }

    public void setRemainingCreditAmount(final String remainingCreditAmount) {
        if (remainingCreditAmount == null) {
            this.remainingCreditAmount = "";
        } else {
            this.remainingCreditAmount = remainingCreditAmount;
        }
    }

    public void setCreditLimitChanged(final String creditLimitChanged) {
        if (creditLimitChanged == null) {
            this.creditLimitChanged = "";
        } else {
            this.creditLimitChanged = creditLimitChanged;
        }
    }

    public void setProductName(final String productName) {
        if (productName == null) {
            this.productName = "";
        } else {
            this.productName = productName;
        }
    }

    public void setRequestTypeId(final String type) {
        if (type == null) {
            this.requestTypeId = "";
        } else {
            this.requestTypeId = type;
        }
    }

    public void setRequestStatus(final String status) {
        if (status == null) {
            this.requestStatus = "";
        }else {
            this.requestStatus = status;
        }
    }

}
