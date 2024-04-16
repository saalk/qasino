package cloud.qasino.games.database.repository;

import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.Visitor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    //@Query("SELECT * FROM playerS p WHERE p.game_id = ?1")
    List<Player> findByGameOrderBySeatAsc(Game game);

    //@Query("SELECT * FROM playerS p ORDER BY CREATED desc WHERE p.visitor_id = ?1")
    List<Player> findByGameOrderByCreatedDesc(Game game);


    //@Query("SELECT count(p) FROM playerS p WHERE p.game_id = ?1")
    int countByGame(Game game);

    @Query(
            value = "SELECT * FROM \"player\" ORDER BY player_id",
            countQuery = "SELECT count(*) FROM player",
            nativeQuery = true)
    Page<Visitor> findAllPlayersWithPage(Pageable pageable);

}
