package applyextra.commons.model.database.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "CORRELATION")
@EqualsAndHashCode(exclude = {"id", "creationTime", "externalReference"})
public class CorrelationEntity {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_CC_REQUEST")
    private CreditCardRequestEntity request;

    @Column(name = "CORRELATION_TYPE")
    private String correlationType;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATION_TIME")
    private Date creationTime;

    @Column(name = "EXTERNAL_REFERENCE")
    private String externalReference;
}
