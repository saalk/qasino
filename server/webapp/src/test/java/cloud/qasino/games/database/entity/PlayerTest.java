package cloud.qasino.games.database.entity;

import cloud.qasino.games.simulator.QasinoSimulator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PlayerTest extends QasinoSimulator {

    @Test
    public void givenQasinoPlayer_whenCreated_thenReturnValidObjectValues() {

        assertThat(playerVisitor.getVisitor()).isEqualTo(visitor);
        assertThat(playerVisitor.getFiches()).isEqualTo(99);
        assertThat(playerVisitor.getSeat()).isEqualTo(99);
        // However ai players are no visitors!
        assertThat(playerVisitor.isHuman()).isEqualTo(true);
        assertThat(playerVisitor.isWinner()).isEqualTo(false);

        assertNull(bot.getVisitor());
        assertThat(bot.getFiches()).isEqualTo(99);
        assertThat(bot.getSeat()).isEqualTo(99);
        // However ai players are no visitors!
        assertThat(bot.isHuman()).isEqualTo(false);
        assertThat(bot.isWinner()).isEqualTo(false);

    }
}