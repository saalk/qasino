package applyextra.commons.model.database.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import applyextra.commons.configuration.RequestType;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "CC_PROCESS_SPECIFIC_DATA")
@EqualsAndHashCode(exclude = {"id", "creationTime", "requestType", "referenceValue"})
public class ProcessSpecificDataEntity {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_CC_REQUEST")
    private CreditCardRequestEntity request;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATION_TIME")
    private Date creationTime;

    @Column(name = "REFERENCE_KEY")
    private String referenceKey;

    @Column(name = "REFERENCE_VALUE")
    private String referenceValue;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "REQUEST_TYPE")
    private RequestType requestType;

    public String toString() {
        return referenceKey + "=" + referenceValue;
    }
}
