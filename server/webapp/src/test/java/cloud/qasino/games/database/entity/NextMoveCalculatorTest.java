package cloud.qasino.games.database.entity;

import cloud.qasino.games.simulator.QasinoSimulator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class NextMoveCalculatorTest extends QasinoSimulator {

    @Test
    public void givenQasinoCalculateNextMove_whenCreated_thenReturnValidObjectValues() {

        assertThat(playing.getPlayer().getPlayerId()).isEqualTo(playerVisitor.getPlayerId());
        assertThat(playing.getCurrentMoveNumber()).isEqualTo(1);
        assertThat(playing.getCurrentRoundNumber()).isEqualTo(1);

    }
}