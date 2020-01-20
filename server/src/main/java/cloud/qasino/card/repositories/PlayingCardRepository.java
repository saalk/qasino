package cloud.qasino.card.repositories;

import cloud.qasino.card.entity.Game;
import cloud.qasino.card.entity.Player;
import cloud.qasino.card.entity.PlayingCard;
import cloud.qasino.card.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayingCardRepository extends JpaRepository<PlayingCard, Integer> {

    List<PlayingCard> findByGame(int gameId);
    Long countByGame(int gameId);

    @Query(
            value = "SELECT * FROM PLAYINGCARDS ORDER BY ID",
            countQuery = "SELECT count(*) FROM PLAYINGCARDS",
            nativeQuery = true)
    List<User> findAllPlayingCardsByGameWithPage(Pageable pageable);

    List<PlayingCard> findByGameOrderBySequenceAsc(Game game);


    Optional<PlayingCard> findByCard(String card);
}
