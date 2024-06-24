package cloud.qasino.games.database.repository;

import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.League;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeagueRepository extends JpaRepository<League, Long> {

    // counts
    //@Query("SELECT count(u) FROM League u where u.LeagueName = ?1")
    Long countByName(String leagueName);

    String COUNT_LEAGUES_FOR_INITIATOR = "SELECT count(*) FROM \"league\" as l WHERE l.\"visitor_id\" = :initiator";
    @Query(value = COUNT_LEAGUES_FOR_INITIATOR, nativeQuery = true)
    Integer countLeaguesForInitiator(@Param(value = "initiator") String initiator);

    // find one
    League findOneByLeagueId(Long leagueId);
    Optional<League> findLeagueByLeagueId(Long leagueId);
    Optional<League> findLeagueByNameAndNameSequence(String leagueName, int leagueNameSequence);

    public final static String FIND_LEAGUES_FOR_VISITOR_ID =
            "SELECT * FROM \"league\" a WHERE a.\"visitor_id\" = :visitorId " +
                    "AND a.\"is_active\" = CAST('true' AS BOOLEAN) ";
    public final static String COUNT_LEAGUES_FOR_VISITOR_ID =
            "SELECT count(*) FROM \"league\" a WHERE a.\"visitor_id\" = :visitorId " +
                    "AND a.\"is_active\" = CAST('true' AS BOOLEAN) ";

    @Query(value = FIND_LEAGUES_FOR_VISITOR_ID, countQuery = COUNT_LEAGUES_FOR_VISITOR_ID, nativeQuery = true)
    public List<League> findLeaguesForVisitorWithPage(
            @Param("visitorId") long visitorId,
            Pageable pageable);
}
