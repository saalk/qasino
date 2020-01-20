package cloud.qasino.card.statemachine;

import cloud.qasino.card.domain.qasino.statemachine.GameState;
import cloud.qasino.card.domain.qasino.statemachine.GameTrigger;
import com.github.oxo42.stateless4j.StateMachine;
import com.github.oxo42.stateless4j.StateMachineConfig;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Component
@Scope("prototype")
@Slf4j
public class QasinoStateMachine {

	// @formatter:on
	private StateMachine<GameState, GameTrigger> sm;

	public void initialize(final StateMachineConfig<GameState, GameTrigger> config, final GameState currentState) {
		sm = new StateMachine<>(currentState, config);
	}

	public void initialize(final StateMachineConfig<GameState, GameTrigger> config) {
		this.initialize(config, GameState.INITIALIZED);
	}

	public void check(final GameState state) {
		if (!sm.getState()
				     .equals(state)) {
			throw new IllegalStateException("Unexpected state found: " + sm.getState() + " instead of: " + state);
		}
	}

	public void checkAll(final List<GameState> states) {

		boolean found = false;
		for (GameState state : states) {
			if (sm.getState().equals(state)) {
				found = true;
			}
		}
		if (!found)
			throw new IllegalStateException("Unexpected state found: " + sm.getState() + " instead of: " + states.toString());
	}

	public String transition(final GameTrigger trigger) {

		if (!sm.canFire(trigger)) {
			return null;
		}

		sm.fire(trigger);
		return sm.getState()
				       .toString();
	}

	public String getCurrentState() {
		return getCurrentStateEnum().toString();
	}

	public GameState getCurrentStateEnum() {
		return sm.getState();
	}

}
