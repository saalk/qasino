package applyextra.commons.components.retry;

import lombok.Getter;
import lombok.Setter;
import applyextra.commons.configuration.RequestType;
import applyextra.commons.state.CreditCardsStateMachine.State;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Represents the criteria to use to select requests to be retried
 */
@Getter
public class RetryCriteria {

    @Setter
    private RequestType requestTypeCriteria;

    private final Set<State> pendingRequestStateCriteria = new LinkedHashSet<>();

    private final Set<State> errorAndPreviousStateCriteria = new LinkedHashSet<>();
}
