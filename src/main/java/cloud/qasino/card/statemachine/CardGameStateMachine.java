package cloud.qasino.card.statemachine;

import com.github.oxo42.stateless4j.StateMachine;
import com.github.oxo42.stateless4j.StateMachineConfig;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@Component
@Scope("prototype")
@Slf4j
public class CardGameStateMachine {
	// @formatter:off
	public enum State {
		// PRE
		SELECTED,
		HAS_HUMAN,
		IS_SHUFFLED,
		// MAIN
		TURN_STARTED,
		PLAYING,
		TURN_ENDED,
		// POST
		GAME_WON,
		NO_WINNER,
		
		// MISC
		ERROR
	}
	
	public enum Trigger {
		
		// PRE
		POST_HUMAN,
		POST_AI,
		
		POST_INIT,
		PUT_INIT,
		POST_INIT_HUMAN,
		
		POST_SETUP_HUMAN,
		DELETE_SETUP_HUMAN,
		
		POST_SETUP_AI,
		DELETE_SETUP_AI,
		
		PUT_SETUP_PLAYER,
		
		POST_SHUFFLE,
		
		// MAIN
		PUT_DEAL_TURN,
		PUT_PLAYING_TURN,
		PUT_PASS_TURN,
		
		// POST
		PLAYER_WINS,
		ROUNDS_ENDED,
		NO_CARDS_LEFT,
		
		// MISC
		OK,
		ERROR,
		GET,
		DELETE
	}
	
	// @formatter:on
	private StateMachine<State, Trigger> sm;
	
	public void initialize(final StateMachineConfig<State, Trigger> config, final State currentState) {
		sm = new StateMachine<>(currentState, config);
	}
	
	public void initialize(final StateMachineConfig<State, Trigger> config) {
		this.initialize(config, State.SELECTED);
	}
	
	public void check(final State state) {
		if (!sm.getState()
				     .equals(state)) {
			throw new IllegalStateException("Unexpected state found: " + sm.getState() + " instead of: " + state);
		}
		log.info(String.format("CardGameStateMachine checkAll state to check: %s", state.toString()));
		log.info(String.format("CardGameStateMachine checkAll state found: %s", sm.getState().toString()));
	}
	
	public void checkAll(final List<State> states) {
		
		log.info(String.format("CardGameStateMachine checkAll states to check: %s", states.toString()));
		log.info(String.format("CardGameStateMachine checkAll state found: %s", sm.getState().toString()));
		
		boolean found = false;
		for (State state : states) {
			if (sm.getState().equals(state)) {
				found = true;
			}
		}
		if (!found)
			throw new IllegalStateException("Unexpected state found: " + sm.getState() + " instead of: " + states.toString());
	}
	
	public String transition(final Trigger trigger) {
		log.info(String.format("CardGameStateMachine transition trigger is: %s", trigger));
		
		if (!sm.canFire(trigger)) {
			log.info(String.format("CardGameStateMachine transition cannot fire trigger"));
			return null;
		}
		
		sm.fire(trigger);
		log.info(String.format("CardGameStateMachine transition trigger fired new state is: %s", sm.getState().toString()));
		
		return sm.getState()
				       .toString();
	}
	
	public String getCurrentState() {
		return getCurrentStateEnum().toString();
	}
	
	public State getCurrentStateEnum() {
		return sm.getState();
	}
	
}
