package cloud.qasino.card.repositories;

import cloud.qasino.card.entity.PlayingCard;
import cloud.qasino.card.entity.Player;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends CrudRepository<Player, Integer> {

    //List<Player> findByGame(int gameId);

}
