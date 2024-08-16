package cloud.qasino.games.database.entity;

import cloud.qasino.games.simulator.QasinoSimulator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResultTest extends QasinoSimulator {

    @Test
    public void givenQasinoResult_whenCreated_thenReturnValidObjectValues() {

        assertThat(playerVisitorResult.getFichesWon()).isEqualTo(50);
        assertThat(playerVisitorResult.getGame()).isEqualTo(game);
        assertThat(playerVisitorResult.getPlayer()).isEqualTo(playerVisitor);
        assertThat(playerVisitorResult.getVisitor()).isEqualTo(visitor);

    }
}