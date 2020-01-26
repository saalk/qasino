package applyextra.commons.model.database.entity;

import lombok.Getter;
import lombok.Setter;
import applyextra.commons.model.database.converter.LocalDateAtributeConverter;
import applyextra.commons.model.database.converter.LocalDateTimeAtributeConverter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "CC_LOAN_AGREEMENTS")
public class CreditCardLoanAgreementEntity {

    public enum Status {
        ACTIVE,
        CLOSED
    }

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "UUID")
    private String uuid;

    @Column(name = "RGB")
    private String rgb;

    @Column(name = "BBAN")
    private String bban;

    @Column(name = "MAIN_AGREEMENT")
    private String mainAgreement;

    @Convert(converter = LocalDateAtributeConverter.class)
    @Column(name = "AGREEMENT_END_DATE")
    private LocalDate agreementEndDate;

    @Convert(converter = LocalDateAtributeConverter.class)
    @Column(name = "FIRST_DOWN_PAYMENT_DATE")
    private LocalDate firstDownPaymentDate;

    @Convert(converter = LocalDateAtributeConverter.class)
    @Column(name = "LOAN_END_DATE")
    private LocalDate loanEndDate;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "AGREEMENT_STATUS")
    private Status agreementStatus;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "LOAN_AGREEMENT_STATUS")
    private Status loanAgreementStatus;

    @Column(name = "CREDIT_CARD_NUMBER")
    private String creditCardNumber;

    @Convert(converter = LocalDateTimeAtributeConverter.class)
    @Column(name = "CREATION_TIME")
    private LocalDateTime creationTime;

    @Convert(converter = LocalDateTimeAtributeConverter.class)
    @Column(name = "UPDATE_TIME")
    private LocalDateTime updateTime;

}
