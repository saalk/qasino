package applyextra.commons.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import nl.ing.sc.creditcardmanagement.checkcreditcardcreditscore1.jaxb.generated.AggregatedCreditLimitType;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@ToString
@Table(name = "EXISTING_LOAN")
public class ExistingLoanAgreement {

    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "FK_PERSON_ID")
    private String personId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATA_EXPIRE_TIME")
    private Date expireTime;

    @Column(name = "AGREEMENT_NUMBER")
    private String agreementNumber;

    @Column(name = "AGREEMENT_TYPE")
    private String agreementType;

    @Column(name = "AGGREGATED_CREDIT_LIMIT")
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private String creditLimit;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FIRST_DOWNPAYMENT_DATE")
    private Date firstDownPaymentDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CALC_FINAL_DOWNPAYMENT_DATE")
    private Date calcFinalDownPaymentDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "REAL_FINAL_DOWNPAYMENT_DATE")
    private Date realFinalDownPaymentDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "REGISTRATION_DATE")
    private Date registrationDate;

    @Transient
    private List<CreditArrear> creditAgreementArrears;

    @Column(name = "REMARK")
    private String remark;

    @Column(name = "HOLDERSHIP")
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private String holdership;

    @Transient
    public AggregatedCreditLimitType getCreditLimit() {
        final AggregatedCreditLimitType result = new AggregatedCreditLimitType();
        String[] parts = creditLimit.split("[|]");
        result.setCurrency(parts[0]);
        result.setAmount(new BigDecimal(parts[1]));
        result.setEstimatedBKRMonthlyAmount(new BigDecimal(parts[2]));
        return result;
    }

    @Transient
    public void setCreditLimit(final AggregatedCreditLimitType creditLimit) {
        this.creditLimit = creditLimit.getCurrency()
                + '|' + creditLimit.getAmount()
                + '|' + creditLimit.getEstimatedBKRMonthlyAmount();
    }

    @Transient
    public Object getHoldership() {
        return holdership;
    }

    @Transient
    public void setHoldership(final Object holdership) {
        this.holdership = String.valueOf(holdership);
    }

}
