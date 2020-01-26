package applyextra.commons.model.database.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Ellen Heuven on 19-12-2018
 */

@Getter
@Setter
@Entity
@Table(name = "CC_CHANGE_PROCESSES")
@Slf4j
@NoArgsConstructor
public class ChangeProcessEntity {

    public enum Status {
        MANUAL_HANDLING,
        FUTURE,
        NOT_HANDLED,
        PROCESSING, PROCESSED, PARTIALLY_PROCESSED,
        MISSING_DATA,
        NOT_APPLICABLE,
        ERROR,
        FAILED;
    }

    public enum ProcessName {
        CHANGE_ADDRESS,
        CHANGE_ADDRESS_BKR,
        CHANGE_NAME,
        CHANGE_PRICING,
        CHANGE_PRICING_STUDENTS
    }

    @Id
    @Setter(AccessLevel.PACKAGE)
    @Column(name = "MESSAGE_ID")
    private String messageId;

    @Column(name = "RGB")
    private String rgb;

    @Column(name = "IBAN")
    private String iban;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FUTURE_DATE")
    private Date futureDate;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "PROCESS_NAME")
    private ProcessName processName;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "STATUS")
    private Status status;

    @Lob
    @Column(name = "PROCESS_RESULT")
    private String processResult;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATION_TIME")
    private Date creationTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATE_TIME")
    private Date lastUpdate;

    @OneToOne(fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "FK_NOTIFICATION")
    private ChangeNotificationEntity notificationEntity;

    public ChangeProcessEntity(final String messageId) {
        log.debug("Handling message with messageID: " + messageId);
        this.messageId = messageId;
        this.creationTime = new Date();
        this.lastUpdate = new Date();
    }

    @Override
    public String toString() {
        return this.processName+" "+this.rgb;
    }
}
