package cloud.qasino.games.database.repository;

import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Integer> {

    //@Query("SELECT * FROM PLAYERS p WHERE p.GAME_ID = ?1")
    List<Player> findByGameOrderBySeatAsc(Game game);

    //@Query("SELECT * FROM PLAYERS p ORDER BY CREATED desc WHERE p.USER_ID = ?1")
    List<Player> findByGameOrderByCreatedDesc(Game game);


    //@Query("SELECT count(p) FROM PLAYERS p WHERE p.GAME_ID = ?1")
    int countByGame(Game game);

    @Query(
            value = "SELECT * FROM PLAYERS ORDER BY PLAYER_ID",
            countQuery = "SELECT count(*) FROM PLAYERS",
            nativeQuery = true)
    Page<User> findAllPlayersWithPage(Pageable pageable);

}
