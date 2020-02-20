package applyextra.commons.model.database.entity;

import lombok.Getter;
import lombok.Setter;
import applyextra.commons.model.financialdata.CardType;
import nl.ing.sc.creditcardmanagement.commonobjects.PortfolioCode;

import javax.persistence.*;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "CC_ACCOUNT_STATUS")
/*
  The data in this entity is not to be used for new requests
 */
public class AccountStatusEntity {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "CARDTYPE")
    @Enumerated(EnumType.STRING)
    private CardType cardType;

    @Column(name = "PORTFOLIOCODE")
    @Enumerated(EnumType.STRING)
    private PortfolioCode portfolioCode;

    @Column(name = "CREDITLIMIT")
    private Integer creditLimit;
    
    @Column(name = "CREDITLIMIT_REQ")
    private Integer requestedCreditLimit;
    
    @Column(name = "BALANCE_AMOUNT")
    private BigDecimal balanceAmount;
}
