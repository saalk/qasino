package cloud.qasino.card.database.repository;

import cloud.qasino.card.database.entity.CardMove;
import cloud.qasino.card.database.entity.Turn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardMoveRepository extends JpaRepository<CardMove, Integer> {


    List<CardMove> findByTurn(Turn turn);

    //@Query("select e from Turn e where e.gameId = :gameId")
    //Stream<Turn> findByGameIdReturnStream(Game game);


}
