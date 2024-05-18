package cloud.qasino.games.database.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class TurnTest extends QasinoSimulator {

    @Test
    public void givenQasinoTurn_whenCreated_thenReturnValidObjectValues() {

        assertThat(turn.getActivePlayerId()).isEqualTo(player.getPlayerId());
        assertThat(turn.getCurrentTurnNumber()).isEqualTo(1);
        assertThat(turn.getCurrentRoundNumber()).isEqualTo(1);

    }
}