package applyextra.commons.model.database.entity;

import lombok.Getter;
import lombok.Setter;
import applyextra.commons.components.refund.RefundData;
import applyextra.commons.components.refund.RefundDataConverter;
import applyextra.commons.model.refund.RefundType;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "REFUND_ITEM")
public class RefundItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Integer id;

    @Convert(converter = RefundDataConverter.class)
    @Column(name = "REFUND_DATA", nullable = false)
    private RefundData refundData;

    @Enumerated(EnumType.STRING)
    @Column(name = "REFUND_TYPE", nullable = false)
    private RefundType type;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATION_TIME", nullable = false)
    private Date creationTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_CC_REQUEST")
    private CreditCardRequestEntity request;

}
