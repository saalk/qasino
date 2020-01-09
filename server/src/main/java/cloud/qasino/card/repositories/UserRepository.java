package cloud.qasino.card.repositories;

import cloud.qasino.card.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    //@Query("SELECT count(u) FROM USERS u where u.ALIAS = ?1")
    Long countByAlias(String alias);

    @Query(
            value = "SELECT * FROM USERS ORDER BY USER_ID",
            countQuery = "SELECT count(*) FROM USERS",
            nativeQuery = true)
    Page<User> findAllUsersWithPage(Pageable pageable);

}
