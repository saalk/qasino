package cloud.qasino.games.statemachine.trigger;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GameTriggerTest {

    GameTrigger gameTrigger = GameTrigger.VALIDATE;

    @Test
    void fromLabel() {
        assertEquals(GameTrigger.VALIDATE,GameTrigger.fromLabel("prepare"));

    }

    @Test
    void fromLabelWithDefault() {
    }

    @Test
    void getLabel() {
    }
}