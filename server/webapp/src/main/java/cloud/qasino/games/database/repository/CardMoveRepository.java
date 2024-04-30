package cloud.qasino.games.database.repository;

import cloud.qasino.games.database.entity.CardMove;
import cloud.qasino.games.database.entity.Turn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardMoveRepository extends JpaRepository<CardMove, Long> {


    List<CardMove> findByTurnOrderBySequenceAsc(Turn turn);

    //@Query("select e from Turn e where e.gameId = :gameId")
    //Stream<Turn> findByGameIdReturnStream(Game game);


}
