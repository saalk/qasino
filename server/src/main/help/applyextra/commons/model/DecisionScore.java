package applyextra.commons.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Slf4j
@Table(name = "DECISION_SCORE")
public class DecisionScore {

    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "FK_PERSON_ID")
    private String personId;

    @Column(name = "NAME")
    private String name;

    @Column(name = "RESULT")
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private String result;

    @Column(name = "REASON")
    private String reason;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LAST_UPDATED")
    private Date lastUpdated;

    @Transient
    public void setResult(final CreditScoreResult creditScoreResult) {
        result = creditScoreResult.toString();
    }

    @Transient
    public CreditScoreResult getResult() {

        try {
            return CreditScoreResult.valueOf(result.toUpperCase());
        } catch (final IllegalArgumentException e)  {
            log.warn("Unknown CreditScoreResult: " + result, e);
            return CreditScoreResult.MISSING;
        }
    }
}
