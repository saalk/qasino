package cloud.qasino.card.repositories;

import cloud.qasino.card.entity.Event;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends CrudRepository<Event, Integer> {

    List<Event> findAllForGameId(int gameId);

}
