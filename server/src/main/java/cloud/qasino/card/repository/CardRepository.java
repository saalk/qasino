package cloud.qasino.card.repository;

import cloud.qasino.card.entity.Card;
import cloud.qasino.card.entity.Game;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Integer> {

    public final static String FIND_CARDS_BY_GAME_ID =
            "SELECT * FROM CARD " +
                    "WHERE GAME_ID = :gameId ";
    public final static String COUNT_CARDS_BY_GAME_ID =
            "SELECT count(*) FROM CARD  " +
                    "WHERE GAME_ID = :gameId ";

    // counters
    Long countByGame(int gameId);

    // lists
    List<Card> findByGame(Game game);
    List<Card> findByCard(String card);

    @Query( value = FIND_CARDS_BY_GAME_ID,
            countQuery = COUNT_CARDS_BY_GAME_ID,
            nativeQuery = true)
    List<Card> findAllCardsByGameWithPage(@Param("gameId") int gameId, Pageable pageable);

    List<Card> findByGameOrderByLocationAscSequenceAsc(Game game);
}
