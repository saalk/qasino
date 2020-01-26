package applyextra.commons.model.database.entity;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Ellen Heuven on 20-12-2018
 */

@Getter
@Setter
@Entity
@Table(name = "CC_CHANGE_NOTIFICATION")
@Slf4j
@NoArgsConstructor
public class ChangeNotificationEntity {

    @Setter(AccessLevel.PACKAGE)
    @Id
    @Column(name = "MESSAGE_ID")
    private String messageId;

    @Setter(AccessLevel.PACKAGE)
    @Lob
    @Column(name = "NOTIFICATION")
    private String notification;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATION_TIME")
    private Date creationTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATE_TIME")
    private Date lastUpdate;

    @OneToOne(mappedBy = "notificationEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private ChangeProcessEntity changeProcessEntity;

    public ChangeNotificationEntity(final String notification, final String messageId) {
        this.messageId = messageId;
        this.notification = notification;
        this.creationTime = new Date();
        this.lastUpdate = new Date();
    }

    @Override
    public String toString() {
        return this.messageId;
    }
}
