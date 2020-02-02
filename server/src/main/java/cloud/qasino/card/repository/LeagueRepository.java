package cloud.qasino.card.repository;

import cloud.qasino.card.entity.League;
import cloud.qasino.card.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeagueRepository extends JpaRepository<League, Integer> {

    //@Query("SELECT count(u) FROM USERS u where u.ALIAS = ?1")
    //Long countLeagueByActive(boolean active);

    List<League>  findAllByUser(User user);


    @Query(
            value = "SELECT * FROM USERS ORDER BY USER_ID",
            countQuery = "SELECT count(*) FROM USERS",
            nativeQuery = true)
    List<League> findAllLeaguesWithPage(Pageable pageable);

}
