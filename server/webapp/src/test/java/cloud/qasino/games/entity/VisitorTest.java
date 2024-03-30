package cloud.qasino.games.entity;

import cloud.qasino.games.database.entity.Visitor;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class VisitorTest {

    @Test
    public void whenCalledGetVisitorName_thenCorrect() {
        Visitor visitor = new Visitor("Julie", 1, "julie@domain.com");

        assertThat(visitor.getVisitorName()).isEqualTo("Julie");
    }

    @Test
    public void whenCalledGetEmail_thenCorrect() {
        Visitor visitor = new Visitor("Julie", 1, "julie@domain.com");

        assertThat(visitor.getEmail()).isEqualTo("julie@domain.com");
    }

    @Test
    public void whenCalledSetVisitorName_thenCorrect() {
        Visitor visitor = new Visitor("Julie", 9, "julie@domain.com");

        visitor.setVisitorName("John");

        assertThat(visitor.getVisitorName()).isEqualTo("John");
    }

    @Test
    public void whenCalledSetEmail_thenCorrect() {
        Visitor visitor = new Visitor("Julie", 2, "julie@domain.com");

        visitor.setEmail("john@domain.com");

        assertThat(visitor.getEmail()).isEqualTo("john@domain.com");
    }

}
