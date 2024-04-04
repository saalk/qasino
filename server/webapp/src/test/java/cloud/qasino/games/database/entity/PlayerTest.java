package cloud.qasino.games.database.entity;

import cloud.qasino.games.database.entity.enums.player.AiLevel;
import cloud.qasino.games.database.entity.enums.player.Role;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PlayerTest extends QasinoSimulator {

    @Test
    public void givenQasinoPlayer_whenCreated_thenReturnValidObjectValues() {

        assertThat(player.getVisitor()).isEqualTo(visitor);
        assertThat(player.getFiches()).isEqualTo(50);
        assertThat(player.getSeat()).isEqualTo(1);
        // However ai players are no visitors!
        assertThat(player.isHuman()).isEqualTo(true);
        assertThat(player.isWinner()).isEqualTo(false);

        assertNull(bot.getVisitor());
        assertThat(bot.getFiches()).isEqualTo(50);
        assertThat(bot.getSeat()).isEqualTo(2);
        // However ai players are no visitors!
        assertThat(bot.isHuman()).isEqualTo(false);
        assertThat(bot.isWinner()).isEqualTo(false);

    }
}