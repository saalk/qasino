package cloud.qasino.games.database.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ResultTest extends QasinoSimulator {

    @Test
    public void givenQasinoResult_whenCreated_thenReturnValidObjectValues() {

        assertThat(result.getFichesWon()).isEqualTo(50);
        assertThat(result.getGame()).isEqualTo(game);
        assertThat(result.getPlayer()).isEqualTo(player);
        assertThat(result.getVisitor()).isEqualTo(visitor);

    }
}