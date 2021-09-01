package cloud.qasino.quiz.repository;

import cloud.qasino.quiz.entity.Game;
import cloud.qasino.quiz.entity.Turn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TurnRepository extends JpaRepository<Turn, Integer> {


    List<Turn> findByGame(Game game);

    //@Query("select e from Turn e where e.gameId = :gameId")
    //Stream<Turn> findByGameIdReturnStream(Game game);


}
