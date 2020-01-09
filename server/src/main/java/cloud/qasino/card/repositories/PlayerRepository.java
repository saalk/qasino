package cloud.qasino.card.repositories;

import cloud.qasino.card.entity.Game;
import cloud.qasino.card.entity.Player;
import cloud.qasino.card.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Integer> {

    //@Query("SELECT * FROM PLAYERS p WHERE p.GAME_ID = ?1")
    List<Player> findByGameOrderBySequenceAsc(Game game);

    //@Query("SELECT * FROM PLAYERS p ORBER BY CREATED desc WHERE p.USER_ID = ?1")
    //Player findTopByOrderByCreatedDescByGame_Id(int gameId);


    //@Query("SELECT count(p) FROM PLAYERS p WHERE p.GAME_ID = ?1")
    int countByGame(Game game);

    @Query(
            value = "SELECT * FROM PLAYERS ORDER BY PLAYER_ID",
            countQuery = "SELECT count(*) FROM PLAYERS",
            nativeQuery = true)
    Page<User> findAllPlayersWithPage(Pageable pageable);


}
