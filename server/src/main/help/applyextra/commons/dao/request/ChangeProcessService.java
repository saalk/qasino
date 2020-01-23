package applyextra.commons.dao.request;

import lombok.extern.slf4j.Slf4j;
import applyextra.commons.model.Message;
import applyextra.commons.model.database.entity.ChangeNotificationEntity;
import applyextra.commons.model.database.entity.ChangeProcessEntity;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static applyextra.commons.model.database.entity.ChangeProcessEntity.ProcessName.*;
import static applyextra.commons.model.database.entity.ChangeProcessEntity.ProcessName.CHANGE_PRICING;
import static applyextra.commons.model.database.entity.ChangeProcessEntity.Status.*;

/**
 * @author Praveena Biyyam on 11-5-2018
 */
@Slf4j
@Service
@Transactional
@Lazy
public class ChangeProcessService {

    @Resource
    private MessageRepository messageRepository;

    @Resource
    private ChangeProcessRepository changeProcessRepository;

    @Resource ChangeNotificationRepository changeNotificationRepository;

    public Message saveMessage(final Message message) {
        Message retval = messageRepository.save(message);
        messageRepository.flush();
        return retval;
    }

    public ChangeProcessEntity saveChangeProcessMessage(final ChangeProcessEntity changeProcessEntity){
        ChangeProcessEntity entity = changeProcessRepository.save(changeProcessEntity);
        changeProcessRepository.flush();
        return entity;
    }

    public ChangeNotificationEntity saveChangeNotification(final ChangeNotificationEntity changeNotificationEntity){
        ChangeNotificationEntity entity = changeNotificationRepository.save(changeNotificationEntity);
        changeNotificationRepository.flush();
        return entity;
    }

    public List<Message> findMessagesToRetryByObserver() {
        return messageRepository.findMessageByStatusNameAndRetry(Message.Status.NOT_HANDELED_BY_OBSERVER.name(), true);
    }

    public Message findByMessageId(final String messageId) {
        return messageRepository.findMessageByMessageId(messageId);
    }

    public ChangeProcessEntity findByChangeProcessId(final String messageId) {
        return changeProcessRepository.findMessageByMessageId(messageId);
    }

    public Message findByMessageIdAndParentId(final String messageId, final String parentId) {
        return messageRepository.findMessageByMessageIdAndParentId(messageId, parentId);
    }

    public Message findByParentIdAndObserverName(final String parentId, final String observerName) {
        return messageRepository.findMessageByParentIdAndObserverName(parentId, observerName);
    }

    public int deleteMessagesByParentId(String parentId) {
        return messageRepository.deleteByParentId(parentId);
    }

    public int deleteMessagesByMessageId(String messageId) {
        return messageRepository.deleteByMessageId(messageId);
    }

    public int deleteChangeMessagesByMessageId(String messageId) {
        return changeProcessRepository.deleteByMessageId(messageId);
    }

    public int deleteMessagesByCreationDate(final Date creationTime) {
        return messageRepository.deleteByCreationTime(creationTime);
    }

    public int deleteChangeMessagesByCreationDate(final Date creationTime) {
        return changeProcessRepository.deleteByCreationTime(creationTime);
    }

    public List<Message> findMessagesByObserverName(final String observerName) {
        return messageRepository.findMessagesByObserverName(observerName);
    }

    public List<Message> findMessagesByTopicName(final String topicName) {
        return messageRepository.findMessagesByTopicName(topicName);
    }

    public List<ChangeProcessEntity> findChangeMessagesByProcessName(final ChangeProcessEntity.ProcessName processName) {
        return changeProcessRepository.findMessagesByProcessName(processName);
    }

    /**
     * Returns all the records that are scheduled to processed today for the given process.
     * N.B. the process name uses like instead of equal.
     *
     * @param processName name of the process to look for
     * @return List of records where FUTURE_DATE < tomorrow and STATUS = FUTURE
     */
    public List<ChangeProcessEntity> findChangeMessagesScheduledUntilToday(final ChangeProcessEntity.ProcessName processName) {
        final Date tomorrow = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).plusDays(1).toInstant());
        return changeProcessRepository.findByProcessNameAndStatusIsAndFutureDateBefore(processName, FUTURE, tomorrow);
    }

    public int deleteMessagesByTopicName(final String topicName) {
        int retval = messageRepository.deleteByTopicName(topicName);
        messageRepository.flush();
        return retval;
    }

    public int deleteMessagesByProcessName(final ChangeProcessEntity.ProcessName processName) {
        int entity = changeProcessRepository.deleteByProcessName(processName);
        changeProcessRepository.flush();
        return entity;
    }

    public List<ChangeProcessEntity> findByChangeStatus(final ChangeProcessEntity.Status status) {
        return changeProcessRepository.findMessagesByStatus(status);
    }

    public List<ChangeProcessEntity> findByChangeRgb(final String rgb) {
        return changeProcessRepository.findMessagesByRgb(rgb);
    }

    public List<ChangeProcessEntity> findByChangeIban(final String iban) {
        return changeProcessRepository.findMessagesByIban(iban);
    }


    public ChangeNotificationEntity findNotificationByMessageId(final String messageId) {
        return changeNotificationRepository.findNotificationByMessageId(messageId);
    }

    public int deleteNotificationByMessageId(final String messageId) {
        return changeNotificationRepository.deleteByMessageId(messageId);
    }

    public int deleteNotificationByCreationDate(final Date creationDate) {
        return changeNotificationRepository.deleteByCreationTime(creationDate);
    }

    public List<ChangeProcessEntity> getChangeProcessHistoricalRequests(String id, ChangeProcessEntity.ProcessName processName, int numberOfDays) {
        if(CHANGE_PRICING.equals(processName)) {
            return changeProcessRepository.findChangePricings(id, processName, PROCESSED, PARTIALLY_PROCESSED, getTimeToCheck(numberOfDays));
        } else {
            return changeProcessRepository.findChanges(id, processName, PROCESSED, PARTIALLY_PROCESSED, getTimeToCheck(numberOfDays));
        }
    }

    private Date getTimeToCheck(int numberOfDays) {
        return Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).minusDays(numberOfDays).toInstant());
    }

}
