package cloud.qasino.games.database.repository;

import cloud.qasino.games.database.entity.CardMove;
import cloud.qasino.games.database.entity.Playing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardMoveRepository extends JpaRepository<CardMove, Long> {

    // finds
    @Query(value = "SELECT * FROM \"cardmove\" where \"playing_id\" = :playingId ORDER BY \"sequence\" ASC ", nativeQuery = true)
    List<CardMove> findByPlayingIdOrderBySequenceAsc(long playingId);
    List<CardMove> findByPlayingOrderBySequenceAsc(Playing playing);
    List<CardMove> findByPlayerIdOrderBySequenceAsc(long playerId);

}
