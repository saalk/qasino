package cloud.qasino.games.database.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class NextMoveCalculatorTest extends QasinoSimulator {

    @Test
    public void givenQasinoCalculateNextMove_whenCreated_thenReturnValidObjectValues() {

        assertThat(turn.getActivePlayer().getPlayerId()).isEqualTo(player.getPlayerId());
        assertThat(turn.getCurrentMoveNumber()).isEqualTo(1);
        assertThat(turn.getCurrentRoundNumber()).isEqualTo(1);

    }
}