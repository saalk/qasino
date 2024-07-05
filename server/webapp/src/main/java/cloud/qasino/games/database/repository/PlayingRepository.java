package cloud.qasino.games.database.repository;

import cloud.qasino.games.database.entity.CardMove;
import cloud.qasino.games.database.entity.GamingTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayingRepository extends JpaRepository<GamingTable, Long> {

    // finds
    @Query(value = "SELECT * FROM \"turn\" where \"game_id\" = :gameId ", nativeQuery = true)
    List<GamingTable> findByGameId(Long gameId);

    // finds
    @Query(value = "SELECT * FROM \"cardmove\" where \"gamingtable_id\" = :gamingtableId ORDER BY \"sequence\" ASC ", nativeQuery = true)
    List<CardMove> findByGamingTableIdOrderBySequenceAsc(long gamingtableId);
    List<CardMove> findByGamingTableOrderBySequenceAsc(GamingTable gamingTable);
    List<CardMove> findByPlayerIdOrderBySequenceAsc(long playerId);


}
