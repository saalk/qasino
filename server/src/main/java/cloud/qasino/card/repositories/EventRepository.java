package cloud.qasino.card.repositories;

import cloud.qasino.card.entity.Event;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends CrudRepository<Event, Integer> {

    //List<User> findByAlias(String alias);

}
