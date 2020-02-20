package applyextra.commons.model.database.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "CC_REJECTED_RULES")
@EqualsAndHashCode(exclude = { "id", "ruleCheckingProcess", "rejectCode", "rejectReason", "canBeOverruled" })
public class CreditCardRejectedRulesEntity {
    
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    
    @Column(name = "RULE_ID")
    private String ruleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_CC_REQUEST")
    private CreditCardRequestEntity request;

    @Column(name = "RULE_CHECK_PROCESS")
    private String ruleCheckingProcess;

    @Column(name = "REJECT_CODE")
    private String rejectCode;

    @Column(name = "REJECT_REASON")
    private String rejectReason;

    @Column(name = "OVERRULED")
    @Type(type = "yes_no")
    private boolean canBeOverruled;
}
