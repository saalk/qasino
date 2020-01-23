package cloud.qasino.card.repositories;

import cloud.qasino.card.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Stream;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {


    List<Event> findByGameId(int gameId);

    @Query("select e from Event e where e.gameId = :gameId")
    Stream<Event> findByGameIdReturnStream(@Param("gameId") int gameId);


}
