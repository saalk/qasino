package cloud.qasino.games.database.repository;

import cloud.qasino.games.database.entity.CardMove;
import cloud.qasino.games.database.entity.Turn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardMoveRepository extends JpaRepository<CardMove, Long> {

    // finds
    @Query(value = "SELECT * FROM \"cardmove\" where \"turn_id\" = :turnId ORDER BY \"sequence\" ASC ", nativeQuery = true)
    List<CardMove> findByTurnIdOrderBySequenceAsc(long turnId);
    List<CardMove> findByTurnOrderBySequenceAsc(Turn turn);
    List<CardMove> findByPlayerIdOrderBySequenceAsc(long playerId);

}
