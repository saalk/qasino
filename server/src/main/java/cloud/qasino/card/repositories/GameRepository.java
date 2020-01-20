package cloud.qasino.card.repositories;

import cloud.qasino.card.entity.Game;
import cloud.qasino.card.entity.League;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Integer> {

    public final static String FIND_ALL = "SELECT * FROM GAMES ORDER BY CREATED DESC";
    public final static String COUNT_ALL = "SELECT count(*) FROM GAMES";
    // games having players that have a specific user
    //"AND a.id NOT IN (SELECT c.columnFromA from a.table3Obj c where state = :state)" +
    public final static String FIND_BY_USER_ID_STATE =
            "SELECT * FROM GAMES a RIGHT JOIN a.PLAYERS b " +
                    "WHERE b.USER_ID = :userId" +
                    "AND a.GAME_STATE = :gameState";
    public final static String COUNT_BY_USER_ID_STATE =
            "SELECT count(*) FROM GAMES a RIGHT JOIN a.PLAYERS b " +
                    "WHERE b.USER_ID = :userId" +
                    "AND a.GAME_STATE = :gameState";

    Long countByLeague(League league);

    List<Game> findGamesByLeague(League league);

    @Query(value = FIND_ALL, countQuery = COUNT_ALL, nativeQuery = true)
    List<Game> findAllGamesWithPage(Pageable pageable);

    @Query(value = FIND_BY_USER_ID_STATE, countQuery = COUNT_BY_USER_ID_STATE, nativeQuery = true)
    public List<Game> findGamesByUserIdByStateWithPage(
            @Param("userId") int userId,
            @Param("gameState") String gameState,
            Pageable pageable);


}
