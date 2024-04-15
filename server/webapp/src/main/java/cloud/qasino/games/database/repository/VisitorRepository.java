package cloud.qasino.games.database.repository;

import cloud.qasino.games.database.entity.Visitor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VisitorRepository extends JpaRepository<Visitor, Long> {

    //@Query("SELECT count(u) FROM VISITORS u where u.VisitorName = ?1")
    Long countByVisitorName(String visitorName);

    Optional<Visitor> findVisitorById(Long id);
    Optional<Visitor> findVisitorByVisitorNameAndVisitorNameSequence(String visitorName, int visitorNameSequence);


    @Query(
            value = "SELECT * FROM VISITORS ORDER BY VISITOR_ID",
            countQuery = "SELECT count(*) FROM VISITORS",
            nativeQuery = true)
    List<Visitor> findAllVisitorsWithPage(Pageable pageable);

}
