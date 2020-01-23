package applyextra.commons.model.database.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "CC_ACCOUNT")
public class CreditCardAccountEntity {
	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(name = "ACCOUNT_NUMBER")
	private String accountNumber;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "FK_PARENT_ACCOUNT")
	private CreditCardAccountEntity parentAccount;

	//something like for the family structure
	/*@OneToMany(mappedBy="parentAccount", cascade = CascadeType.ALL)
	private Set<CreditCardAccountEntity> childrenAccount;*/

	@Column(name = "IBAN")
	private String iban;

	@Column(name = "ACCOUNT_HOLDER")
	private String accountHolder;

    @Column(name = "SUB_ARRANGEMENT_NUMBER")
    private String subArrangmentNumber;
	
    @Column(name = "CARD_HOLDER")
    private String cardHolder;
    
    @Column(name = "ARRANGEMENT_NUMBER")
    private String arrangementNumber;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="FK_CREDITCARD")
	private CreditCardEntity creditCard;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="FK_ACCOUNT_STATUS")
	private AccountStatusEntity accountStatus;
}
