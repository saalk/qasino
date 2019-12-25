package cloud.qasino.card.repository;

import cloud.qasino.card.entity.Deck;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeckRepository extends CrudRepository<Deck, Integer> {
}
