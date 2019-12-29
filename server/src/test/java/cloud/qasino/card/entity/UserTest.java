package cloud.qasino.card.entity;

import org.junit.Ignore;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserTest {

	@Test
	public void whenCalledGetAlias_thenCorrect() {
		User user = new User("Julie", "julie@domain.com");

		assertThat(user.getAlias()).isEqualTo("Julie");
	}

	@Test
	public void whenCalledGetEmail_thenCorrect() {
		User user = new User("Julie", "julie@domain.com");

		assertThat(user.getEmail()).isEqualTo("julie@domain.com");
	}

	@Test
	public void whenCalledSetAlias_thenCorrect() {
		User user = new User("Julie", "julie@domain.com");

		user.setAlias("John");

		assertThat(user.getAlias()).isEqualTo("John");
	}

	@Test
	public void whenCalledSetEmail_thenCorrect() {
		User user = new User("Julie", "julie@domain.com");

		user.setEmail("john@domain.com");

		assertThat(user.getEmail()).isEqualTo("john@domain.com");
	}

}
