package applyextra.commons.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import applyextra.commons.dao.request.DecisionScoreServiceImpl;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = "PERSONS")
public class Person {

    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "FK_REQUEST_ID")
    private String requestId;
    //todo: match column
//    private CreditCardRequestEntity request;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LAST_UPDATED")
    private Date lastUpdated;

    @Column(name = "PARTY_ID")
    private String partyId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "BIRTHDATE")
    private Date birthDate;

    @Column(name = "INITIALS")
    private String initials;

    @Column(name = "PREFIX")
    private String prefix;

    @Column(name = "LASTNAME")
    private String lastName;

    @Column(name = "INCOMESOURCE")
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private String incomeSource;

    @Column(name = "MONTHLY_NET_INCOME")
    private BigDecimal monthlyNetIncome;

    @Column(name = "MONTHLY_ALIMONY")
    private BigDecimal monthlyAlimony;

    @Column(name = "HAS_CHILDREN")
    private Boolean childrenPresent;

    @Column(name = "YEAR_FULL_INCOME")
    private Boolean incomeFullLastYear;

    @Transient
    public SourceOfIncome getSourceOfIncome() {
        return SourceOfIncome.fromCode(incomeSource);
    }

    @Transient
    public void setSourceOfIncome(final SourceOfIncome sourceOfIncome) {
        incomeSource = sourceOfIncome.getCode();
    }

    /**
     * gets the overall decision score after getting all score checks from database
     *
     * @return CreditScoreResult
     */
    @Transient
    @Deprecated
    public CreditScoreResult getOverallDecisionScore(final List<DecisionScore> decisionScores) {
        return DecisionScoreServiceImpl.getOverallDecisionScore(decisionScores);
    }

    public Person copyIndividualInformation(final Person master) {
        this.setId(master.getId());
        this.setInitials(master.getInitials());
        this.setPrefix(master.getPrefix());
        this.setLastName(master.getLastName());
        this.setBirthDate(master.getBirthDate());
        return this;
    }

    /**
     * Copies the financial information from the master to the slave
     */
    public Person copyFinancialInformation(final Person master) {
        this.setSourceOfIncome(master.getSourceOfIncome());
        this.setMonthlyNetIncome(master.getMonthlyNetIncome());
        this.setIncomeFullLastYear(master.getIncomeFullLastYear());
        this.setChildrenPresent(master.getChildrenPresent());
        this.setMonthlyAlimony(master.getMonthlyAlimony());
        return this;
    }

}
