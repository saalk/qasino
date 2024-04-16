package cloud.qasino.games.database.repository;

import cloud.qasino.games.database.entity.League;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeagueRepository extends JpaRepository<League, Long> {

    public final static String FIND_ACTIVE_LEAGUE_BY_VISITOR_ID =
            "SELECT * FROM \"league\" a WHERE a.visitor_id = :visitorId " +
                    "AND a.is_active = 'TRUE' ";
    public final static String COUNT_ACTIVE_LEAGUE_BY_VISITOR_ID =
            "SELECT count(*) FROM \"league\" a WHERE a.visitor_id = :visitorId " +
                    "AND a.is_active = 'TRUE' ";

    @Query(value = FIND_ACTIVE_LEAGUE_BY_VISITOR_ID, countQuery = COUNT_ACTIVE_LEAGUE_BY_VISITOR_ID, nativeQuery = true)
    public List<League> findAllActiveLeagueForVisitorWithPage(
            @Param("visitorId") long visitorId,
            Pageable pageable);
}
