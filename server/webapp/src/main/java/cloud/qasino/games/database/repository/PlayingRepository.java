package cloud.qasino.games.database.repository;

import cloud.qasino.games.database.entity.Playing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayingRepository extends JpaRepository<Playing, Long> {

    // finds
    @Query(value = "SELECT * FROM \"playing\" where \"game_id\" = :gameId ", nativeQuery = true)
    List<Playing> findByGameId(Long gameId);

}
