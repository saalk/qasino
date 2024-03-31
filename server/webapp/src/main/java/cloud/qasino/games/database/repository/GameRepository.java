package cloud.qasino.games.database.repository;

import cloud.qasino.games.statemachine.GameState;
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
public interface GameRepository extends JpaRepository<Game, Integer> {

    // prepared queries
    public final static String FIND_ALL = "SELECT * FROM GAME ORDER BY CREATED DESC";
    public final static String COUNT_ALL = "SELECT count(*) FROM GAME";
    public final static String COUNT_TODAY =
            "SELECT count(*) FROM GAME g " +
                    "WHERE g.YEAR = :year " +
                    "AND g.MONTH = :month " +
                    "AND g.DAY = :day ";
    public final static String COUNT_WEEK =
            "SELECT count(*) FROM GAME g " +
                    "WHERE g.YEAR = :year " +
                    "AND g.WEEK = :week ";
    public final static String COUNT_MONTH =
            "SELECT count(*) FROM GAME g " +
                    "WHERE g.YEAR = :year " +
                    "AND g.MONTH = :month ";

    public final static String FIND_NEWGAMES_BY_VISITOR_ID =
            "SELECT * FROM GAME a JOIN PLAYER b " +
                    "WHERE a.GAME_ID = b.GAME_ID " +
                    "AND b.VISITOR_ID = :visitorId " +
                     "AND a.STATE IN ('NEW','PENDING_INVITATIONS','PREPARED') ";
    public final static String COUNT_NEWGAMES_BY_VISITOR_ID =
            "SELECT count(*) FROM GAME a JOIN PLAYER b " +
                    "WHERE a.GAME_ID = b.GAME_ID " +
                    "AND b.VISITOR_ID = :visitorId " +
                    "AND a.STATE IN ('NEW','PENDING_INVITATIONS','PREPARED') ";

    public final static String FIND_STARTEDGAMES_BY_VISITOR_ID =
            "SELECT * FROM GAME a JOIN PLAYER b " +
                    "WHERE a.GAME_ID = b.GAME_ID " +
                    "AND b.VISITOR_ID = :visitorId " +
                    "AND a.STATE IN ('PLAYING') ";
    public final static String COUNT_STARTEDGAMES_BY_VISITOR_ID =
            "SELECT count(*) FROM GAME a JOIN PLAYER b " +
                    "WHERE a.GAME_ID = b.GAME_ID " +
                    "AND b.VISITOR_ID = :visitorId " +
                    "AND a.STATE IN ('PLAYING') ";

    public final static String FIND_FINISHEDGAMES_BY_VISITOR_ID =
            "SELECT * FROM GAME a JOIN PLAYER b " +
                    "WHERE a.GAME_ID = b.GAME_ID " +
                    "AND b.VISITOR_ID = :visitorId " +
                    "AND a.STATE IN ('PLAYING') ";
    public final static String COUNT_FINISHEDGAMES_BY_VISITOR_ID =
            "SELECT count(*) FROM GAME a JOIN PLAYER b " +
                    "WHERE a.GAME_ID = b.GAME_ID " +
                    "AND b.VISITOR_ID = :visitorId " +
                    "AND a.STATE IN ('PLAYING') ";
    // counts
    Integer countByLeague(League league);

    @Query(value = "SELECT count(*) FROM GAME g WHERE g.STATE IN (:states)", nativeQuery = true)
    Integer countByStates(@Param(value = "states") String[] states);

    @Query(value = COUNT_TODAY, nativeQuery = true)
    Integer countByToday(String year, String month, String day);

    @Query(value = COUNT_WEEK, nativeQuery = true)
    Integer countByThisWeek(String year, String week);

    @Query(value = COUNT_MONTH, nativeQuery = true)
    Integer countByThisMonth(String year, String month);


    // special finds
    List<Game> findGamesByLeague(League league);

    @Query(value = "SELECT * FROM GAME g WHERE g.STATE IN :states", nativeQuery = true)
    List<Game> findActiveGameNodeStates(@Param(value = "states") List<GameState> states);


    // list with paging
    @Query(value = FIND_ALL, countQuery = COUNT_ALL, nativeQuery = true)
    List<Game> findAllGamesWithPage(Pageable pageable);

    @Query(value = FIND_NEWGAMES_BY_VISITOR_ID, countQuery = COUNT_NEWGAMES_BY_VISITOR_ID, nativeQuery = true)
    public List<Game> findAllNewGamesForVisitorWithPage(
            @Param("visitorId") int visitorId,
            Pageable pageable);

    @Query(value = FIND_STARTEDGAMES_BY_VISITOR_ID, countQuery = COUNT_STARTEDGAMES_BY_VISITOR_ID, nativeQuery = true)
    public List<Game> findAllStartedGamesForVisitorWithPage(
            @Param("visitorId") int visitorId,
            Pageable pageable);

    @Query(value = FIND_FINISHEDGAMES_BY_VISITOR_ID, countQuery = COUNT_FINISHEDGAMES_BY_VISITOR_ID, nativeQuery = true)
    public List<Game> findAllFinishedGamesForVisitorWithPage(
            @Param("visitorId") int visitorId,
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
        DateTimeFormatter week = DateTimeFormatter.ofPattern("W");
        return String.valueOf(LocalDate.now().format(week));
    }

    // todo implement
    //List<Game> getCurrentGamesForVisitorByState(Game currentGame, List<Type> typesToCancel,
    //                                          List<GameState> pendingStates);

    // todo implement
    //Game getLastTypeGameByVisitor(Type contextType, int visitorId);
}
