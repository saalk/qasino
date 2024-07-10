package cloud.qasino.games.database.entity;

import cloud.qasino.games.simulator.QasinoSimulator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResultTest extends QasinoSimulator {

    @Test
    public void givenQasinoResult_whenCreated_thenReturnValidObjectValues() {

        assertThat(result.getFichesWon()).isEqualTo(40);
        assertThat(result.getGame()).isEqualTo(game);
        assertThat(result.getPlayer()).isEqualTo(playerVisitor);
        assertThat(result.getVisitor()).isEqualTo(null);

    }
}