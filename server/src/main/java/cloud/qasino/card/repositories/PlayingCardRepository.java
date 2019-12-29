package cloud.qasino.card.repositories;

import cloud.qasino.card.entity.PlayingCard;
import cloud.qasino.card.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayingCardRepository extends CrudRepository<PlayingCard, Integer> {

    //List<PlayingCard> findByGame(int gameId);
}
