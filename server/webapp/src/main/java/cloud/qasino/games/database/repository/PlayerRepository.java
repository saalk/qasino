package cloud.qasino.games.database.repository;

import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    // @formatter:off

    // BASIC FINDS
    @Query(value = "SELECT * FROM \"player\" ORDER BY \"player_id\"", countQuery = "SELECT count(*) FROM \"player\"", nativeQuery = true)
    Page<Player> findAllPlayersWithPage(Pageable pageable);

    List<Player> findByGame(Game game);

    // SPECIAL FINDS
    String  FIND_PLAYERS_INVITED_FOR_A_GAME = "SELECT      b.* FROM \"game\" as a JOIN \"player\" as b WHERE a.\"game_id\" = b.\"game_id\" AND b.\"role\" IN ('INVITED','ACCEPTED','REJECTED') AND a.\"game_id\" = :gameId ";
    String COUNT_PLAYERS_INVITED_FOR_A_GAME = "SELECT count(*) FROM \"game\" as a JOIN \"player\" as b WHERE a.\"game_id\" = b.\"game_id\" AND a.\"role\" IN ('INVITED','ACCEPTED','REJECTED') AND a.\"game_id\" = :gameId ";
    @Query(value = FIND_PLAYERS_INVITED_FOR_A_GAME, countQuery = COUNT_PLAYERS_INVITED_FOR_A_GAME, nativeQuery = true)
    public List<Player> findAllPlayersInvitedForAGame(@Param("gameId") long gameId, Pageable pageable);

    String FIND_PLAYERS_FOR_A_GAME_ORDER_BY_SEAT = "SELECT * FROM \"player\" p WHERE p.\"game_id\" = :game_id ORDER BY \"seat\" ASC";
    List<Player> findByGameOrderBySeatAsc(Game game);
    String FIND_PLAYERS_FOR_A_GAME_ORDER_BY_CREATED = "SELECT * FROM \"player\" p WHERE p.\"game_id\" = :game_id ORDER BY \"created\" ASC";
    List<Player> findByGameOrderByCreatedDesc(Game game);

    // SPECIAL COUNTS
    String COUNT_ALL_PLAYERS_FOR_A_GAME = "SELECT count(*) FROM \"game\" a WHERE a.\"game_id\" = :game_id ";
    int countByGame(Game game);
    String COUNT_ALL_AILEVEL_PLAYERS_FOR_A_GAME = "SELECT count(*) FROM \"game\" a WHERE a.\"game_id\" = :game_id ";

    String COUNT_AILEVEL = "SELECT count(*) FROM \"player\" as p WHERE p.\"is_human\" = :human AND p.\"ai_level\" = :aiLevel ";
    @Query(value = COUNT_AILEVEL, nativeQuery = true)
    Integer countByAiLevel(@Param(value = "human") String human, @Param(value = "aiLevel") String aiLevel);

    String COUNT_BOTS_FOR_A_GAME = "SELECT count(*) FROM \"player\" as p WHERE p.\"is_human\" = false WHERE a.\"game_id\" = b.\"game_id\" ";
    @Query(value = COUNT_BOTS_FOR_A_GAME, nativeQuery = true)
    Integer countBotsForAGame(@Param("gameId") long gameId);

}
