package applyextra.commons.dao.request;

import applyextra.commons.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author Nimal
 */
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findMessageByStatusNameAndRetry(final String statusName, final boolean retry);

    Message findMessageByMessageIdAndParentId(final String messageId, final String parentId);

    Message findMessageByMessageId(final String messageId);

    Message findMessageByParentIdAndObserverName(final String parentId, final String observerName);

    List<Message> findMessagesByObserverName(final String observerName);

    //@Transactional
    List<Message> findMessagesByTopicName(final String topicName);

    @Transactional
    @Modifying
    @Query(value = "delete from Message m where m.topicName = ?1")
    int deleteByTopicName(final String topicName);

    @Modifying
    @Transactional
    @Query(value = "delete from Message m where m.parentId = ?1")
    Integer deleteByParentId(String parentId);

    @Modifying
    @Transactional
    @Query(value = "delete from Message m where m.messageId = ?1")
    Integer deleteByMessageId(String messageId);

    @Modifying
    @Transactional
    @Query(value = "delete from Message m where m.creationTime > :currentDate")
    Integer deleteByCreationTime(@Param("currentDate") final Date currentDate);
}
