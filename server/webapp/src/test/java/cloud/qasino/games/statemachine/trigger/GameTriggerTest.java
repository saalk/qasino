package cloud.qasino.games.statemachine.trigger;

import cloud.qasino.games.database.entity.enums.game.Type;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class GameTriggerTest {

    GameTrigger gameTrigger = GameTrigger.PREPARE;

    @Test
    void fromLabel() {
        assertEquals(GameTrigger.PREPARE,GameTrigger.fromLabel("prepare"));

    }

    @Test
    void fromLabelWithDefault() {
    }

    @Test
    void getLabel() {
    }
}