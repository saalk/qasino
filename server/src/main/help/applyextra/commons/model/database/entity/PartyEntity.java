package applyextra.commons.model.database.entity;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "PARTIES")
public class PartyEntity {

    @Column(name = "PARTY_ID")
	@Id
    private String partyId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LAST_UPDATED")
    private Date lastUpdated;

    @OneToMany(cascade =  CascadeType.ALL)
    @JoinColumn(name = "FK_PARTY_ID")
    private List<DecisionScoreEntity> decisionScoreEntities = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "FK_PARTY_ID")
    private List<ExistingLoanEntity> existingLoanEntities = new ArrayList<>();

}
