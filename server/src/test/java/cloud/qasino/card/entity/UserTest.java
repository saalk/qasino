package cloud.qasino.card.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserTest {

    @Test
    public void whenCalledGetUserName_thenCorrect() {
        User user = new User("Julie", 1, "julie@domain.com");

        assertThat(user.getUserName()).isEqualTo("Julie");
    }

    @Test
    public void whenCalledGetEmail_thenCorrect() {
        User user = new User("Julie", 1, "julie@domain.com");

        assertThat(user.getEmail()).isEqualTo("julie@domain.com");
    }

    @Test
    public void whenCalledSetUserName_thenCorrect() {
        User user = new User("Julie", 9, "julie@domain.com");

        user.setUserName("John");

        assertThat(user.getUserName()).isEqualTo("John");
    }

    @Test
    public void whenCalledSetEmail_thenCorrect() {
        User user = new User("Julie", 2, "julie@domain.com");

        user.setEmail("john@domain.com");

        assertThat(user.getEmail()).isEqualTo("john@domain.com");
    }

}
