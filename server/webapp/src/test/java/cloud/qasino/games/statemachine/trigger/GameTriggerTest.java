package cloud.qasino.games.statemachine.trigger;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

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