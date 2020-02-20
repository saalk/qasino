package applyextra.commons.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * @author Nimal
 */
@Getter
@Setter
@Entity
@ToString
@Table(name = "DISTRIBUTION_MESSAGE", indexes = {@Index(name = "MEDSSAGE_ID_INDEX", columnList = "MESSAGE_ID")})
@Slf4j
public class Message {

    public static final int MAX_ERROR_MESSAGE_SIZE = 500;

    public enum Status {
        INITIATED,
        NOT_HANDELED_BY_OBSERVER,
        PROCESSED
    }

    public enum ErrorCause {
        NONE,
        UNFORSEEN_ERROR,
        CONNECTION_ERROR,
        MESSAGE_CORRUPT,
        FUNCTIONAL_BACKEND_ERROR
    }

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "MESSAGE_ID")
    private String messageId;

    @Column(name = "PARENT_ID")
    private String parentId = UUID.randomUUID().toString();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATION_TIME")
    private Date creationTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LAST_UPDATE")
    private Date lastUpdate;

    @Column(name = "MESSAGE_CONTENTS", columnDefinition = "CLOB")
    private String messageContents;

    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    @Column(name = "TOPIC_NAME")
    private String topicName;

    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    @Column(name = "STATUS_NAME")
    private String statusName;

    @Setter(AccessLevel.PRIVATE)
    @Column(name = "OBSERVER_NAME")
    private String observerName;

    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    @Column(name = "ERROR_CAUSE")
    private String errorCauseName;

    @Column(name = "ERROR_MESSAGE")
    private String errorMessage;

    @Column(name = "ERROR_DETAILS", columnDefinition = "CLOB")
    private String errordetails;

    @Column(name = "RETRY")
    @Type(type = "yes_no")
    private boolean retry;

    @Column(name = "NR_RETRIALS")
    private int nrRetrials;

    public Message() {
    }

    public Message(final String messageId, final String topic, String message, final String observer) {
        log.debug("Handling message with messageID: " + messageId);
        topicName = topic;
        creationTime = new Date();
        lastUpdate = new Date();
        statusName = Status.INITIATED.name();
        errorCauseName = ErrorCause.NONE.name();
        messageContents = message;
        this.messageId = messageId;
        setObserverName(observer);
    }

    private void setObserverName(String observer) {
        //this.observerName = ClassUtils.getShortName(observer.getClass());
        this.observerName = observer;
    }


    public Message with(final Status status) {
        this.statusName = status.name();
        return this;
    }

    public Message with(final ErrorCause errorCause) {
        this.errorCauseName = errorCause.name();
        return this;
    }

    public Message withRetry() {
        this.retry = true;
        return this;
    }

    public String getTopic() {
        return topicName;
    }

    public Status getStatus() {
        return Status.valueOf(statusName);
    }

    public ErrorCause getErrorCause() {
        return ErrorCause.valueOf(errorCauseName);
    }

    public Map<String, Object> getMessage() {
        final ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(messageContents, Map.class);
        } catch (final IOException e) {
            throw new IllegalStateException("message can not be instantiated from JSON", e);
        }
    }

    public void setErrorMessage(String errorMessage) {
        if(errorMessage != null) {
            if (errorMessage.length() > MAX_ERROR_MESSAGE_SIZE) {
                this.errorMessage = errorMessage.substring(0, MAX_ERROR_MESSAGE_SIZE);
            } else {
                this.errorMessage = errorMessage;
            }
        }
    }

    public String toString() {
        return "Message " + messageId + " for observer " + observerName + " observing " + topicName;
    }
}
