package applyextra.commons.model.database.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import applyextra.commons.configuration.RequestType;
import applyextra.commons.state.CreditCardsStateMachine.State;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@ToString
@Table(name = "CC_REQUEST")
public class CreditCardRequestEntity {
	@Id
	@Column(name = "ID")
	private String id;

	@Version
	@Column(name = "SEQUENCE_NUMBER")
	private int sequenceNumber;

	@Enumerated(EnumType.STRING)
	@Column(name = "REQUEST_TYPE")
	private RequestType requestType;

	@Enumerated(EnumType.STRING)
	@Column(name = "CURRENT_STATE")
	private State currentState;

	@Enumerated(EnumType.STRING)
	@Column(name = "PREVIOUS_STATE")
	private State previousState;

	@Column(name = "REQUESTOR_ID")
	private String requestorId;

	@Column(name = "CUSTOMER_ID")
	private String customerId;

	@Column(name = "BENEFICIARY_ID")
	private String beneficiaryId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATION_TIME")
	private Date creationTime;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "UPDATE_TIME")
	private Date updateTime;

	@Column(name = "CHANNEL_TYPE")
	private String channelType;
	
    @Column(name = "CHANNEL_SUB_TYPE")
    private String channelSubType;

	@Column(name = "LEGAL_ENTITY")
    private String legalEntity;

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "FK_CC_ACCOUNT")
    private CreditCardAccountEntity account;

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "FK_FINANCIAL_DATA")
    private FinancialDataEntity financialData;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "request", orphanRemoval = true,cascade = CascadeType.ALL)
	private Set<CorrelationEntity> correlations = new HashSet<>();
    
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "request", orphanRemoval = true,cascade = CascadeType.ALL)
    private Set<ProcessSpecificDataEntity> processSpecificData = new HashSet<>();
    
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "request", orphanRemoval = true,cascade = CascadeType.ALL)
    private List<CreditCardRejectedRulesEntity> rejectedRulesList;

    // TODO: make table in database ACCP and PROD. then enable.
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "request", orphanRemoval = true,cascade = CascadeType.ALL)
    private List<RefundItemEntity> refundItems;
}
