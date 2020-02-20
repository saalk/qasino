package cloud.qasino.card.repository;

import cloud.qasino.card.entity.League;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeagueRepository extends JpaRepository<League, Integer> {

    public final static String FIND_ACTIVE_LEAGUES_BY_USER_ID =
            "SELECT * FROM LEAGUES a WHERE a.USER_ID = :userId " +
                    "AND a.IS_ACTIVE = 'TRUE' ";
    public final static String COUNT_ACTIVE_LEAGUES_BY_USER_ID =
            "SELECT count(*) FROM LEAGUES a WHERE a.USER_ID = :userId " +
                    "AND a.IS_ACTIVE = 'TRUE' ";

    @Query(value = FIND_ACTIVE_LEAGUES_BY_USER_ID, countQuery = COUNT_ACTIVE_LEAGUES_BY_USER_ID, nativeQuery = true)
    public List<League> findAllActiveLeaguesForUserWithPage(
            @Param("userId") int userId,
            Pageable pageable);
}
