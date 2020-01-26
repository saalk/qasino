package applyextra.commons.dao.request;

import applyextra.commons.model.database.entity.ChangeProcessEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author Ellen Heuven on 20-12-2018
 */
public interface ChangeProcessRepository extends JpaRepository<ChangeProcessEntity, String> {

    List<ChangeProcessEntity> findMessagesByStatus(final ChangeProcessEntity.Status status);
    List<ChangeProcessEntity> findMessagesByRgb(final String rgb);
    List<ChangeProcessEntity> findMessagesByIban(final String iban);
    List<ChangeProcessEntity> findMessagesByProcessName(final ChangeProcessEntity.ProcessName processName);
    ChangeProcessEntity findMessageByMessageId(final String messageId);

    @Query(value = "select m from ChangeProcessEntity m where m.processName =?1 and m.status = ?2 and m.futureDate < ?3")
    List<ChangeProcessEntity> findByProcessNameAndStatusIsAndFutureDateBefore(final ChangeProcessEntity.ProcessName processName, final ChangeProcessEntity.Status status, final Date untilDay);

    @Transactional
    @Modifying
    @Query(value = "delete from ChangeProcessEntity m where m.processName = ?1")
    int deleteByProcessName(final ChangeProcessEntity.ProcessName topicName);

    @Modifying
    @Transactional
    @Query(value = "delete from ChangeProcessEntity m where m.messageId = ?1")
    Integer deleteByMessageId(String messageId);

    @Modifying
    @Transactional
    @Query(value = "delete from ChangeProcessEntity m where m.creationTime > :currentDate")
    Integer deleteByCreationTime(@Param("currentDate") final Date currentDate);

    @Transactional(readOnly = true)
    @Query(value = "select cpe from ChangeProcessEntity cpe where cpe.rgb = ?1 and cpe.processName= ?2 and (cpe.status=?3 or cpe.status=?4) and cpe.creationTime > ?5")
    List<ChangeProcessEntity> findChanges(String id, ChangeProcessEntity.ProcessName processName, ChangeProcessEntity.Status processed, ChangeProcessEntity.Status partiallyProcessed, Date creationTime);

    @Transactional(readOnly = true)
    @Query(value = "select cpe from ChangeProcessEntity cpe where cpe.iban = ?1 and cpe.processName= ?2 and (cpe.status=?3 or cpe.status=?4) and cpe.creationTime > ?5")
    List<ChangeProcessEntity> findChangePricings(String iban, ChangeProcessEntity.ProcessName processName, ChangeProcessEntity.Status processed, ChangeProcessEntity.Status partiallyProcessed, Date creationTime);

}
