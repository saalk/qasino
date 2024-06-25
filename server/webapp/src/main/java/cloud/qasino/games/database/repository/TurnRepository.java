package cloud.qasino.games.database.repository;

import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Turn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TurnRepository extends JpaRepository<Turn, Long> {

    // finds
    @Query(value = "SELECT * FROM \"turn\" where \"game_id\" = :gameId ", nativeQuery = true)
    List<Turn> findByGameId(Long gameId);

}
