package cloud.qasino.card.repository;

import cloud.qasino.card.entity.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResultsRepository extends JpaRepository<Result, Integer> {

    //@Query("SELECT count(u) FROM USERS u where u.ALIAS = ?1")
    //Long countResultByActive(boolean active);

    List<Result> findAllByPlayer(Player player);
    List<Result> findAllByUser(User user);
    List<Result> findAllByGame(Game game);


    @Query(
            value = "SELECT * FROM RESULTS ORDER BY RESULT_ID",
            countQuery = "SELECT count(*) FROM RESULTS",
            nativeQuery = true)
    List<Result> findAllResultsWithPage(Pageable pageable);

}
