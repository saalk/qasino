package cloud.qasino.games.pattern_statemachine.trigger;

import cloud.qasino.games.pattern.statemachine.event.GameEvent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameEventTest {

    GameEvent gameEvent = GameEvent.VALIDATE;

    @Test
    void fromLabel() {
        assertEquals(GameEvent.VALIDATE, GameEvent.fromLabel("validate"));

    }

    @Test
    void fromLabelWithDefault() {
    }

    @Test
    void getLabel() {
    }
}