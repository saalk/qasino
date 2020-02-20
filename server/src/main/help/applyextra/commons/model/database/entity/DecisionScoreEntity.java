package applyextra.commons.model.database.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import applyextra.commons.model.CreditScoreResult;
import applyextra.commons.orchestration.DecisionScoreType;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@ToString
@Table(name = "CC_DECISION_SCORE")
public class DecisionScoreEntity {

    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "PARTY_ID")
    private String partyId;

    @Column(name = "TYPE")
    @Enumerated(EnumType.STRING)
    private DecisionScoreType type;

    @Column(name = "RESULT")
	@Enumerated(EnumType.STRING)
    private CreditScoreResult result;

    @Column(name = "REASON")
    private String reason;

    //Only for BKR
    @Column(name = "BKRID")
    private String bkrId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATION_DATE")
    private Date date;

    @Column(name =  "CC_REQUEST_ID")
    private String requestId;
}
