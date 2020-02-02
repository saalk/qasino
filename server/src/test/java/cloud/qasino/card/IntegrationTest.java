//package cloud.qasino.playingcard;
//
//import cloud.qasino.playingcard.entity.User;
//import cloud.qasino.playingcard.repository.UserRepository;
//import org.junit.Ignore;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.junit.runner.RunWith;
//
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@RunWith(SpringRunner.class)
//@DataJpaTest
//public class IntegrationTest {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Test @Ignore
//    public void whenCalledSave_thenCorrectNumberOfUsers() {
//        userRepository.save(new User());
//        List<User> users = (List<User>) userRepository.findAll();
//
//        assertThat(users.size()).isEqualTo(1);
//    }
//}