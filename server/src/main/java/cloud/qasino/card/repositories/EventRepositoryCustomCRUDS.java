package cloud.qasino.card.repositories;

import cloud.qasino.card.entity.Event;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Stream;

public interface EventRepositoryCustomCRUDS {

    List<Event> findByGameId(int gameId);

}
