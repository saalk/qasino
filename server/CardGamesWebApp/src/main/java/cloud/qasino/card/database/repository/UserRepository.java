package cloud.qasino.card.database.repository;

import cloud.qasino.card.database.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    //@Query("SELECT count(u) FROM USERS u where u.UserName = ?1")
    Long countByUserName(String userName);

    Optional<User> findUserByUserNameAndUserNameSequence(String userName, int userNameSequence);


    @Query(
            value = "SELECT * FROM USERS ORDER BY USER_ID",
            countQuery = "SELECT count(*) FROM USERS",
            nativeQuery = true)
    List<User> findAllUsersWithPage(Pageable pageable);

}
