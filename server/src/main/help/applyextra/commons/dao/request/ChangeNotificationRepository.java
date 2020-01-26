package applyextra.commons.dao.request;

import applyextra.commons.model.database.entity.ChangeNotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author Ellen Heuven on 20-12-2018
 */
public interface ChangeNotificationRepository extends JpaRepository<ChangeNotificationEntity, String> {

    ChangeNotificationEntity findNotificationByMessageId(final String messageId);

    @Modifying
    @Transactional
    @Query(value = "delete from ChangeNotificationEntity m where m.messageId = ?1")
    Integer deleteByMessageId(String messageId);

    @Modifying
    @Transactional
    @Query(value = "delete from ChangeNotificationEntity m where m.creationTime > :currentDate")
    Integer deleteByCreationTime(@Param("currentDate") final Date currentDate);
}
