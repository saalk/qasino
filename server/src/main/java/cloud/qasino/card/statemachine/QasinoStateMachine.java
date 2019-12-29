package cloud.qasino.card.statemachine;

import com.github.oxo42.stateless4j.StateMachine;
import com.github.oxo42.stateless4j.StateMachineConfig;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@Scope("prototype")
@Slf4j
public class QasinoStateMachine {

	// @formatter:off
	public enum GameState {
		// PRE
		INITIALIZED,	// The game has one Player and the Type and (default) Style is chosen
		PREPARED,		// All is set for the game, all Bots and PLayers are added

		// MAIN
		SHUFFLED,		// The Game has started and PlayingCards are added and (if needed) shuffled
		PLAYING,		// During playing the Events tables records the state of Playing

		// FINISH
		GAME_WON,		// A game is won by a player
		NO_WINNER,		// The game has ended without a winner
		
		// MISC
		TIMEOUT,		// An timeout occurred for external reasons
		ERROR			// An error occurred, a timeout that could not be repleated or a 500
	}
	
	public enum GameTrigger {
		
		// PRE
		SELECT_GAME,	// User has selected a Type of Game with default style
		ADD_DETAILS,	// Player chooses the Style of the Game (optional trigger)
		ADD_BOTS,		// One or more bots are added to the Game

		// MAIN
		SHUFFLE,     	// The Game is shuffled according to the Type
		PLAY,		 	// PLayers are playing the Game

		// FINISH
		ENDED,		 	// The Game is finished and the winner	is known
		ABANDON      	// The main player has quit the game before finishing

	}
	
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
		log.info(String.format("QasinoStateMachine checkAll state to check: %s", state.toString()));
		log.info(String.format("QasinoStateMachine checkAll state found: %s", sm.getState().toString()));
	}
	
	public void checkAll(final List<GameState> states) {
		
		log.info(String.format("QasinoStateMachine checkAll states to check: %s", states.toString()));
		log.info(String.format("QasinoStateMachine checkAll state found: %s", sm.getState().toString()));
		
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
		log.info(String.format("QasinoStateMachine transition trigger is: %s", trigger));
		
		if (!sm.canFire(trigger)) {
			log.info(String.format("QasinoStateMachine transition cannot fire trigger"));
			return null;
		}
		
		sm.fire(trigger);
		log.info(String.format("QasinoStateMachine transition trigger fired new state is: %s", sm.getState().toString()));
		
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
