package applyextra.commons.model.database.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@ToString
@Table(name = "CC_CREDIT_ARREAR")
public class CreditArrearEntity {
    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "TYPE")
    private String type;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CHANGE_DATE")
    private Date startOrChangeDateArrear;
}
