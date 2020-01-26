package applyextra.commons.model.database.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import nl.ing.sc.creditscore.getbkrscore1.jaxb.generated.AggregatedCreditLimitType;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@ToString
@Table(name = "CC_EXISTING_LOANS")
public class ExistingLoanEntity {
    @Id
    @Column(name = "ID")
    private String id;

    @Column(name =  "PARTY_ID")
    private String partyId;

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

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "FK_EXISTING_LOANS")
    private List<CreditArrearEntity> creditArrears = new ArrayList<>();

    @Column(name = "REMARK")
    private String remark;

    @Column(name = "HOLDERSHIP")
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private String holdership;

    @Column(name =  "CC_REQUEST_ID")
    private String requestId;

    @Transient
    public nl.ing.sc.creditcardmanagement.checkcreditcardcreditscore1.jaxb.generated.AggregatedCreditLimitType getOldCreditLimit() {
        final nl.ing.sc.creditcardmanagement.checkcreditcardcreditscore1.jaxb.generated.AggregatedCreditLimitType result = new nl.ing.sc.creditcardmanagement.checkcreditcardcreditscore1.jaxb.generated.AggregatedCreditLimitType();
        String[] parts = creditLimit.split("[|]");
        result.setCurrency(parts[0]);
        result.setAmount(new BigDecimal(parts[1]));
        result.setEstimatedBKRMonthlyAmount(!parts[2].equals("null") ? new BigDecimal(parts[2]): BigDecimal.ZERO);
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
