package cloud.qasino.games.statemachine.trigger;

import cloud.qasino.games.statemachine.event.GameEvent;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GameEventTest {

    GameEvent gameEvent = GameEvent.VALIDATE;

    @Test
    void fromLabel() {
        assertEquals(GameEvent.VALIDATE, GameEvent.fromLabel("prepare"));

    }

    @Test
    void fromLabelWithDefault() {
    }

    @Test
    void getLabel() {
    }
}