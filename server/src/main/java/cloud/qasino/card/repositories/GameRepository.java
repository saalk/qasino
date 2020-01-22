package cloud.qasino.card.repositories;

import cloud.qasino.card.controller.statemachine.GameState;
import cloud.qasino.card.entity.Game;
import cloud.qasino.card.entity.League;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Integer> {

    // prepared queries
    public final static String FIND_ALL = "SELECT * FROM GAMES ORDER BY CREATED DESC";
    public final static String COUNT_ALL = "SELECT count(*) FROM GAMES";
    public final static String COUNT_TODAY =
            "SELECT count(*) FROM GAMES g " +
                    "WHERE g.YEAR = :year " +
                    "AND g.MONTH = :month " +
                    "AND g.DAY = :day ";
    public final static String COUNT_WEEK =
            "SELECT count(*) FROM GAMES g " +
                    "WHERE g.YEAR = :year " +
                    "AND g.WEEK = :week ";
    public final static String COUNT_MONTH =
            "SELECT count(*) FROM GAMES g " +
                    "WHERE g.YEAR = :year " +
                    "AND g.MONTH = :month ";
    // todo LOW use => b.user.userId =
    public final static String FIND_BY_USER_ID_STATE =
            "SELECT * FROM GAMES a RIGHT JOIN a.PLAYERS b " +
                    "WHERE b.USER.USER_ID = :userId " +
                    "AND a.STATE = :gameState ";
    public final static String COUNT_BY_USER_ID_STATE =
            "SELECT count(*) FROM GAMES a RIGHT JOIN a.PLAYERS b " +
                    "WHERE b.USER.USER_ID = :userId " +
                    "AND a.STATE = :gameState ";

    // counts
    Integer countByLeague(League league);

    @Query(value = "SELECT count(*) FROM GAMES g WHERE g.STATE IN :states", nativeQuery = true)
    Integer countByStates(@Param(value = "states") String[] states);

    @Query(value = COUNT_TODAY, nativeQuery = true)
    Integer countByToday(String year, String month, String day);

    @Query(value = COUNT_WEEK, nativeQuery = true)
    Integer countByThisWeek(String year, String week);

    @Query(value = COUNT_MONTH, nativeQuery = true)
    Integer countByThisMonth(String year, String month);

    // special finds
    List<Game> findGamesByLeague(League league);

    @Query(value = "SELECT * FROM GAMES g WHERE g.STATE IN :states", nativeQuery = true)
    List<Game> findActiveGameNodeStates(@Param(value = "states") List<GameState> states);

    // list with paging
    @Query(value = FIND_ALL, countQuery = COUNT_ALL, nativeQuery = true)
    List<Game> findAllGamesWithPage(Pageable pageable);

    @Query(value = FIND_BY_USER_ID_STATE, countQuery = COUNT_BY_USER_ID_STATE, nativeQuery = true)
    public List<Game> findGamesByUserIdByStateWithPage(
            @Param("userId") int userId,
            @Param("gameState") String gameState,
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
}
