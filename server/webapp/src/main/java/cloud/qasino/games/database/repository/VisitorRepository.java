package cloud.qasino.games.database.repository;

import cloud.qasino.games.database.entity.Visitor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VisitorRepository extends JpaRepository<Visitor, Long> {

    //@Query("SELECT count(u) FROM visitorS u where u.VisitorName = ?1")
    Long countByVisitorName(String visitorName);

    Optional<Visitor> findVisitorByVisitorId(Long visitorId);
    Optional<Visitor> findVisitorByVisitorNameAndVisitorNameSequence(String visitorName, int visitorNameSequence);

    @Query(
            value = "SELECT * FROM \"visitor\" ORDER BY \"visitor_id\" ",
            countQuery = "SELECT count(*) FROM \"visitor\" ",
            nativeQuery = true)
    Page<Visitor> findAllVisitorsWithPage(Pageable pageable);

}
