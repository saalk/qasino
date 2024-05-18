package cloud.qasino.games.database.security;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface VisitorRepository extends JpaRepository<Visitor, Long> {

    @Query(
            value = "SELECT * FROM \"visitor\" u WHERE u.\"username\" = ?1",
            nativeQuery = true)
    Visitor findByUsername(@Param("username") String username);

    @Transactional
    void removeUserByUsername(String username);

    Visitor findOneByVisitorId(Long visitorId);
    Visitor findOneByEmail(String email);

    //@Query("SELECT count(u) FROM visitorS u where u.Alias = ?1")
    Long countByAlias(String alias);

    Optional<Visitor> findVisitorByVisitorId(Long visitorId);
    Optional<Visitor> findVisitorByAliasAndAliasSequence(String alias, int aliasSequence);

    @Query(
            value = "SELECT * FROM \"visitor\" ORDER BY \"visitor_id\" ",
            countQuery = "SELECT count(*) FROM \"visitor\" ",
            nativeQuery = true)
    Page<Visitor> findAllVisitorsWithPage(Pageable pageable);

}
