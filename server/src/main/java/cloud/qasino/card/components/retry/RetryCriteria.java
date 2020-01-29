package cloud.qasino.card.components.retry;

import cloud.qasino.card.controller.statemachine.GameState;
import cloud.qasino.card.entity.enums.game.Type;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Represents the criteria to use to select requests to be retried
 */
@Getter
public class RetryCriteria {

    @Setter
    private Type typeCriteria;

    private final Set<GameState> pendingRequestStateCriteria = new LinkedHashSet<>();

    private final Set<GameState> errorAndPreviousStateCriteria = new LinkedHashSet<>();
}
