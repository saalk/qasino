package cloud.qasino.games.database.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class NextMoveCalculatorTest extends QasinoSimulator {

    @Test
    public void givenQasinoCalculateNextMove_whenCreated_thenReturnValidObjectValues() {

        assertThat(gamingTable.getActivePlayer().getPlayerId()).isEqualTo(player.getPlayerId());
        assertThat(gamingTable.getCurrentMoveNumber()).isEqualTo(1);
        assertThat(gamingTable.getCurrentRoundNumber()).isEqualTo(1);

    }
}