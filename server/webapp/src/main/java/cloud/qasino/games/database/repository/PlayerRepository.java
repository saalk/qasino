package cloud.qasino.games.database.repository;

import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.Visitor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    public final static String FIND_PLAYERS_INVITED_FOR_A_GAME =
            "SELECT * FROM \"game\" a JOIN \"player\" b " +
                    "WHERE a.\"game_id\" = b.\"game_id\" " +
                    "AND b.\"role\" IN ('INVITED','ACCEPTED','REJECTED') ";
    public final static String COUNT_PLAYERS_INVITED_FOR_A_GAME =
            "SELECT count(*) FROM \"game\" a JOIN \"player\" b " +
                    "WHERE a.\"game_id\" = b.\"game_id\" " +
                    "AND a.\"role\" IN ('INVITED','ACCEPTED','REJECTED') ";

    //@Query("SELECT * FROM playerS p WHERE p.game_id = ?1")
    List<Player> findByGameOrderBySeatAsc(Game game);


    //@Query("SELECT * FROM player p ORDER BY /"created/" desc WHERE p.visitor_id = ?1")
    List<Player> findByGameOrderByCreatedDesc(Game game);

    //@Query("SELECT count(p) FROM playerS p WHERE p.game_id = ?1")
    int countByGame(Game game);

    //@Query("SELECT * FROM playerS p WHERE p.game_id = ?1")
    List<Player> findByGameId(Long gameId);


    // special finds

    @Query(
            value = "SELECT * FROM \"player\" ORDER BY \"player_id\"",
            countQuery = "SELECT count(*) FROM \"player\"",
            nativeQuery = true)
    Page<Player> findAllPlayersWithPage(Pageable pageable);

    @Query(
            value = FIND_PLAYERS_INVITED_FOR_A_GAME,
            countQuery = COUNT_PLAYERS_INVITED_FOR_A_GAME,
            nativeQuery = true)
    public List<Player> findAllPlayersInvitedForAGame(
            @Param("gameId") long gameId,
            Pageable pageable);
}
