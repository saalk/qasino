package applyextra.commons.model.database.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "CREDITCARD")
public class CreditCardEntity {
	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	
	@Column(name = "CARD_NUMBER")
	private String creditCardNumber;

	@Column(name = "CARD_USER")
	private String cardUser;
	
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "EXPIRATION_DATE")
    private Date expirationDate;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "START_DATE")
    private Date startDate;
    
    @Column(name = "STATUS")
    private String cardStatus;
}
