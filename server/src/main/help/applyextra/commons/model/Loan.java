package applyextra.commons.model;

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
@Table(name = "EXTRA_LOAN")
public class Loan {

    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "FK_PERSON_ID")
    private String personId;

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

    @NotNull(message = "{loan.description.missing}")
    @Column(name = "DESCRIPTION")
    private String description;

}
