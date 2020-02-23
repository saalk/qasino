package cloud.qasino.card.repository;

import cloud.qasino.card.entity.CardMove;
import cloud.qasino.card.entity.Game;
import cloud.qasino.card.entity.Turn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardMoveRepository extends JpaRepository<CardMove, Integer> {


    List<CardMove> findByTurn(Turn turn);

    //@Query("select e from Turn e where e.gameId = :gameId")
    //Stream<Turn> findByGameIdReturnStream(Game game);


}
