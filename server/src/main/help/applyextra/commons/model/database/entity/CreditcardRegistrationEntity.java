package applyextra.commons.model.database.entity;

import lombok.Getter;
import lombok.Setter;
import applyextra.commons.configuration.CreditCardRegistrationsOperation;
import applyextra.commons.configuration.ProcessType;
import applyextra.commons.model.database.converter.LocalDateAtributeConverter;
import applyextra.commons.model.database.converter.LocalDateTimeAtributeConverter;
import applyextra.commons.state.RegistrationStateMachine;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "CC_REGISTRATION_REQUESTS")
public class CreditcardRegistrationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Integer id;

    @Version
    @Column(name = "SEQUENCE_NUMBER")
    private int sequenceNumber;

    @Column(name = "CORRELATION_ID")
    @NotNull(message = "registrations.noCorrelationId")
    private String correlationId;

    @Column(name = "APPLICATION_ID")
    @NotNull(message = "registrations.noApplicationId")
    private String applicationId;

    @Column(name = "ACCOUNT_NUMBER")
    private String siaAccountNumber;

    @Column(name = "USER_ID")
    @NotNull(message = "registrations.userId")
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(name= "OPERATION_TYPE")
    @NotNull(message = "registrations.noOperation")
    private CreditCardRegistrationsOperation creditCardRegistrationsOperation;

    @Enumerated(EnumType.STRING)
    @Column(name= "PROCESS_TYPE")
    @NotNull
    private ProcessType processType;

    @Column(name = "CREDIT_CARD_NUMBER")
    private String creditCardNumber;

    @Column(name = "NEW_VALUE")
    private String newValue;

    @Column(name = "OLD_VALUE")
    private String oldValue;

    @Column(name = "CUSTOMER_ID")
    private String customerId;

    @Column(name = "LOAN_AMOUNT")
    private BigDecimal loanAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "CURRENT_STATE")
    private RegistrationStateMachine.State currentState;

    @Enumerated(EnumType.STRING)
    @Column(name = "PREVIOUS_STATE")
    private RegistrationStateMachine.State previousState;

    @Convert(converter = LocalDateTimeAtributeConverter.class)
    @Column(name = "CREATION_TIME")
    private LocalDateTime creationTime;

    @Convert(converter = LocalDateTimeAtributeConverter.class)
    @Column(name = "UPDATE_TIME")
    private LocalDateTime updateTime;

    @Convert(converter = LocalDateAtributeConverter.class)
    @Column(name = "FIRST_DOWN_PAYMENT_DATE")
    private LocalDate firstDownPaymentDate;

    @Column(name = "CREDIT_BUREAU_CORRELATION_ID")
    private String creditBureauCorrelationId;

}
