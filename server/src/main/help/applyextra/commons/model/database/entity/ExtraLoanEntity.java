package applyextra.commons.model.database.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@Entity
@ToString
@Table(name = "CC_EXTRA_LOAN")
public class ExtraLoanEntity {
    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "PARTY_ID")
    private String partyId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATA_EXPIRE_TIME")
    private Date expireTime;

    @DecimalMin(value = "0", message = "{loan.total.negative}")
    @NotNull(message = "{loan.total.missing}")
    @Column(name = "TOTAL_AMOUNT")
    private BigDecimal totalAmount;

    @DecimalMin(value = "0", message = "{loan.monthly.negative}")
    @NotNull(message = "{loan.monthly.missing}")
    @Column(name = "MONTHLY_AMOUNT")
    private BigDecimal monthlyAmount;

    @NotNull(message = "{loan.counterParty.missing}")
    @Column(name = "COUNTER_PARTY")
    private String counterParty;

    @Column(name =  "DESCRIPTION")
    private String description;

}
