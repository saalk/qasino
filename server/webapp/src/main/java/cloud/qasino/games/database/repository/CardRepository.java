package cloud.qasino.games.database.repository;

import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.Game;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    String FIND_CARDS_BY_GAME_ID =
            "SELECT * FROM \"card\" " +
                    "WHERE \"game_id\" = :gameId ";
    String COUNT_CARDS_BY_GAME_ID =
            "SELECT count(*) FROM \"card\"  " +
                    "WHERE \"game_id\" = :gameId ";

    // counters
    int countByGame(Game game);

    String COUNT_CARDS_FOR_INITIATOR = "SELECT count(*) FROM \"card\" as c JOIN \"game\" as g WHERE c.\"game_id\" = g.\"game_id\" " +
            "AND g.\"initiator\" = :initiator";
    @Query(value = COUNT_CARDS_FOR_INITIATOR, nativeQuery = true)
    Integer countCardsForInitiator(@Param(value = "initiator") String initiator);


    // lists
    List<Card> findByGame(Game game);
    List<Card> findByRankSuit(String rankSuit);
    @Query( value = FIND_CARDS_BY_GAME_ID,
            countQuery = COUNT_CARDS_BY_GAME_ID,
            nativeQuery = true)
    List<Card> findAllCardsByGameWithPage(@Param("gameId") long gameId, Pageable pageable);

    List<Card> findByGameOrderByLocationAscSequenceAsc(Game game);

    List<Card> findByGameOrderBySequenceAsc(Game game);
    @Query(value = "SELECT * FROM \"card\" where \"game_id\" = :gameId ORDER BY \"sequence\" ASC ", nativeQuery = true)
    List<Card> findByGameIdOrderBySequenceAsc(long gameId);
}
