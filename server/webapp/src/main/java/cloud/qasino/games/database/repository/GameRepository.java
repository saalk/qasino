package cloud.qasino.games.database.repository;

import cloud.qasino.games.database.entity.enums.game.GameState;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.League;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    // prepared queries
    public final static String FIND_ALL = "SELECT * FROM \"game\" ORDER BY \"updated\" DESC";
    public final static String COUNT_ALL = "SELECT count(*) FROM \"game\"";
    public final static String FIND_STATES = "SELECT * FROM \"game\" as g WHERE g.\"state\" IN :states";
    public final static String COUNT_STATES = "SELECT count(*) FROM \"game\" as g WHERE g.\"state\" IN (:states)";
    public final static String COUNT_TODAY =
            "SELECT count(*) FROM \"game\" as g " +
                    "WHERE g.\"year\" = :year " +
                    "AND g.\"month\" = :month " +
                    "AND g.\"weekday\" = :weekday ";
    public final static String COUNT_WEEK =
            "SELECT count(*) FROM \"game\" as g " +
                    "WHERE g.\"year\" = :year " +
                    "AND g.\"week\" = :week ";
    public final static String COUNT_MONTH =
            "SELECT count(*) FROM \"game\" g " +
                    "WHERE g.\"year\" = :year " +
                    "AND g.\"month\" = :month ";

    public final static String FIND_ALL_INVITED_BY_VISITOR_ID =
            "SELECT a.* FROM \"game\" as a JOIN \"player\" as b " +
                    "WHERE a.\"game_id\" = b.\"game_id\" " +
                    "AND b.\"visitor_id\" = :visitorId " +
                    "AND a.\"initiator\" != :visitorId " +
                    "ORDER BY a.\"updated\" DESC ";
    public final static String COUNT_ALL_INVITED_BY_VISITOR_ID =
            "SELECT count(a.*) FROM \"game\" as a JOIN \"player\" as b " +
                    "WHERE a.\"game_id\" = b.\"game_id\" " +
                    "AND b.\"visitor_id\" = :visitorId " +
                    "AND a.\"initiator\" != :visitorId ";

    public final static String FIND_ALL_INITIATED_BY_VISITOR_ID =
            "SELECT * FROM \"game\" WHERE \"initiator\" = :visitorId ORDER BY \"updated\" desc ";
    public final static String COUNT_ALL_INITIATED_BY_VISITOR_ID =
            "SELECT count(*) FROM \"game\" WHERE \"initiator\" = :visitorId ";

    public final static String FIND_NEWGAMES_BY_VISITOR_ID =
            "SELECT a.* FROM \"game\" as a JOIN \"player\" b " +
                    "WHERE a.\"game_id\" = b.\"game_id\" " +
                    "AND b.\"visitor_id\" = :visitorId " +
                     "AND a.\"state\" IN ('INITIALIZED','PENDING_INVITATIONS','PREPARED') " +
                    "ORDER BY a.\"updated\" DESC ";
    public final static String COUNT_NEWGAMES_BY_VISITOR_ID =
            "SELECT count(a.*) FROM \"game\" as a JOIN \"player\" b " +
                    "WHERE a.\"game_id\" = b.\"game_id\" " +
                    "AND b.\"visitor_id\" = :visitorId " +
                    "AND a.\"state\" IN ('INITIALIZED','PENDING_INVITATIONS','PREPARED') ";

    public final static String FIND_STARTEDGAMES_BY_VISITOR_ID =
            "SELECT a.* FROM \"game\" as a JOIN \"player\" as b " +
                    "WHERE a.\"game_id\" = b.\"game_id\" " +
                    "AND b.\"visitor_id\" = :visitorId " +
                    "AND a.\"state\" IN ('STARTED','NEXT_MOVE','NEXT_TURN') " +
                    "ORDER BY a.\"updated\" DESC ";
    public final static String COUNT_STARTEDGAMES_BY_VISITOR_ID =
            "SELECT count(a.*) FROM \"game\" as a JOIN \"player\" as b " +
                    "WHERE a.\"game_id\" = b.\"game_id\" " +
                    "AND b.\"visitor_id\" = :visitorId " +
                    "AND a.\"state\" IN ('STARTED','NEXT_MOVE','NEXT_TURN') ";

    public final static String FIND_FINISHEDGAMES_BY_VISITOR_ID =
            "SELECT a.* FROM \"game\" as a JOIN \"player\" as b " +
                    "WHERE a.\"game_id\" = b.\"game_id\" " +
                    "AND b.\"visitor_id\" = :visitorId " +
                    "AND a.\"state\" IN ('FINISHED','QUIT','CANCELLED') " +
                    "ORDER BY a.\"updated\" DESC ";
    public final static String COUNT_FINISHEDGAMES_BY_VISITOR_ID =
            "SELECT count(a.*) FROM \"game\" as a JOIN \"player\" as b " +
                    "WHERE a.\"game_id\" = b.\"game_id\" " +
                    "AND b.\"visitor_id\" = :visitorId " +
                    "AND a.\"state\" IN ('FINISHED','QUIT','CANCELLED') ";

    // counts
    Integer countByLeague(League league);

    @Query(value = COUNT_STATES, nativeQuery = true)
    Integer countByStates(@Param(value = "states") String[] states);

    @Query(value = COUNT_TODAY, nativeQuery = true)
    Integer countByToday(String year, String month, String weekday);

    @Query(value = COUNT_WEEK, nativeQuery = true)
    Integer countByThisWeek(String year, String week);

    @Query(value = COUNT_MONTH, nativeQuery = true)
    Integer countByThisMonth(String year, String month);

    // special finds
    List<Game> findGamesByLeague(League league);

    List<Game> findByInitiator(long initiator);

    // list with paging
    @Query(value = FIND_ALL, countQuery = COUNT_ALL, nativeQuery = true)
    List<Game> findAllGamesWithPage(Pageable pageable);

    @Query(value = FIND_STATES, countQuery = COUNT_STATES,nativeQuery = true)
    List<Game> findActiveGameNodeStates(
            @Param(value = "states") List<GameState> states,
            Pageable pageable);

    @Query(value = FIND_NEWGAMES_BY_VISITOR_ID, countQuery = COUNT_NEWGAMES_BY_VISITOR_ID, nativeQuery = true)
    public List<Game> findAllNewGamesForVisitorWithPage(
            @Param("visitorId") long visitorId,
            Pageable pageable);

    @Query(value = FIND_STARTEDGAMES_BY_VISITOR_ID, countQuery = COUNT_STARTEDGAMES_BY_VISITOR_ID, nativeQuery = true)
    public List<Game> findAllStartedGamesForVisitorWithPage(
            @Param("visitorId") long visitorId,
            Pageable pageable);

    @Query(value = FIND_FINISHEDGAMES_BY_VISITOR_ID, countQuery = COUNT_FINISHEDGAMES_BY_VISITOR_ID, nativeQuery = true)
    public List<Game> findAllFinishedGamesForVisitorWithPage(
            @Param("visitorId") long visitorId,
            Pageable pageable);

    @Query(value = FIND_ALL_INITIATED_BY_VISITOR_ID, countQuery = COUNT_ALL_INITIATED_BY_VISITOR_ID, nativeQuery = true)
    public List<Game> findAllInitiatedGamesForVisitorWithPage(
            @Param("visitorId") long visitorId,
            Pageable pageable);

    @Query(value = FIND_ALL_INVITED_BY_VISITOR_ID, countQuery = COUNT_ALL_INVITED_BY_VISITOR_ID, nativeQuery = true)
    public List<Game> findAllInvitedGamesForVisitorWithPage(
            @Param("visitorId") long visitorId,
            Pageable pageable);

    default String getYear() {
        return String.valueOf(LocalDate.now().getYear());
    }

    default String getMonth() {
        return String.valueOf(LocalDate.now().getMonth());
    }

    default String getDay() {
        return String.valueOf(LocalDate.now().getDayOfMonth());
    }

    default String getWeek() {
        DateTimeFormatter week = DateTimeFormatter.ofPattern("w");
        return String.valueOf(LocalDate.now().format(week));
    }
}
