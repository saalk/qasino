package cloud.qasino.games.database.repository;

import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Turn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TurnRepository extends JpaRepository<Turn, Long> {

    List<Turn> findByGame(Game game);

    //@Query("select e from Turn e where e.gameId = :gameId")
    //Stream<Turn> findByGameIdReturnStream(Game game);


}
