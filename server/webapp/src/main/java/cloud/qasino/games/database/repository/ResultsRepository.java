package cloud.qasino.games.database.repository;

import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Result;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResultsRepository extends JpaRepository<Result, Long> {

    // @formatter:off

    // BASIC FINDS
    @Query(value = "SELECT * FROM \"result\" ORDER BY \"result_id\"", countQuery = "SELECT count(*) FROM \"result\"", nativeQuery = true)
    Page<Result> findAllResultsWithPage(Pageable pageable);
    List<Result> findByGame(Game game);
    @Query(value = "SELECT * FROM \"result\" where \"game_id\" = :gameId ", nativeQuery = true)
    List<Result> findByGameId(Long gameId);

    // SPECIAL FINDS
    public final static String FIND_ACTIVE_RESULT_BY_LEAGUE_ID  =        "SELECT * FROM \"result\" a JOIN \"league\" b JOIN \"game\" c WHERE a.\"game_id\" = c.\"game_id\" AND b.\"league_id\" = c.\"league_id\" AND b.\"league_id\" = :leagueId ";
    public final static String COUNT_ACTIVE_RESULT_BY_LEAGUE_ID = "SELECT count(*) FROM \"result\" a JOIN \"league\" b JOIN \"game\" c WHERE a.\"game_id\" = c.\"game_id\" AND b.\"league_id\" = c.\"league_id\" AND b.\"league_id\" = :leagueId ";
    @Query(value = FIND_ACTIVE_RESULT_BY_LEAGUE_ID, countQuery = COUNT_ACTIVE_RESULT_BY_LEAGUE_ID, nativeQuery = true)
    public List<Result> findAllResultForLeagueWithPage(@Param("leagueId") long leagueId, Pageable pageable);

//    public List<Result> findAllByGame(Game game);
}
