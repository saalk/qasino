package cloud.qasino.games.database.repository;

import cloud.qasino.games.database.entity.Result;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResultsRepository extends JpaRepository<Result, Long> {

    public final static String FIND_ACTIVE_RESULTS_BY_LEAGUE_ID =
            "SELECT * FROM RESULT a JOIN LEAGUE b " +
                    "WHERE a.RESULT_ID = b.RESULT_ID " +
                    "AND b.LEAGUE_ID = :leagueId ";
    public final static String COUNT_ACTIVE_RESULTS_BY_LEAGUE_ID =
            "SELECT count(*) FROM RESULT a JOIN LEAGUE b " +
                    "WHERE a.RESULT_ID = b.RESULT_ID " +
                    "AND b.LEAGUE_ID = :leagueId ";

    @Query(value = FIND_ACTIVE_RESULTS_BY_LEAGUE_ID, countQuery = COUNT_ACTIVE_RESULTS_BY_LEAGUE_ID, nativeQuery = true)
    public List<Result> findAllResultsForLeagueWithPage(
            @Param("leagueId") long leagueId,
            Pageable pageable);

}
