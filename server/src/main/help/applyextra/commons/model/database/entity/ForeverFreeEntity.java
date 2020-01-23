package applyextra.commons.model.database.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Getter
@Setter
@Entity
@Table(name = "CC_FOREVERFREE_ACCOUNTS")
public class ForeverFreeEntity {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "SIA_ACCOUNT_NUMBER")
    private String siaAccountNumber;

}
