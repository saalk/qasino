package applyextra.commons.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@ToString
@Table(name = "CREDIT_ARREAR")
public class CreditArrear {

    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "FK_EXISTING_LOAN_ID")
    private String existingLoanId;

    @Column(name = "TYPE")
    private String type;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CHANGE_DATE")
    private Date startOrChangeDateArrear;

}
