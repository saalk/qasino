package cloud.qasino.card.repositories;

import cloud.qasino.card.entity.Game;
import cloud.qasino.card.entity.PlayingCard;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayingCardRepository extends JpaRepository<PlayingCard, Integer> {

    public final static String FIND_CARDS_BY_GAME_ID =
            "SELECT * FROM PLAYINGCARDS " +
                    "WHERE GAME_ID = :gameId ";
    public final static String COUNT_CARDS_BY_GAME_ID =
            "SELECT count(*) FROM PLAYINGCARDS  " +
                    "WHERE GAME_ID = :gameId ";

    // counters
    Long countByGame(int gameId);

    // lists
    List<PlayingCard> findByGame(Game game);
    List<PlayingCard> findByCard(String card);

    @Query( value = FIND_CARDS_BY_GAME_ID,
            countQuery = COUNT_CARDS_BY_GAME_ID,
            nativeQuery = true)
    List<PlayingCard> findAllPlayingCardsByGameWithPage(@Param("gameId") int gameId,Pageable pageable);

    List<PlayingCard> findByGameOrderByLocationAscSequenceAsc(Game game);
}
