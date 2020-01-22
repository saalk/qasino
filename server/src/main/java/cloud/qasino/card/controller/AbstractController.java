package cloud.qasino.card.controller;

import cloud.qasino.card.controller.statemachine.GameState;
import cloud.qasino.card.controller.statemachine.GameTrigger;
import cloud.qasino.card.controller.statemachine.QasinoStateMachine;
import cloud.qasino.card.domain.qasino.Style;
import cloud.qasino.card.dto.event.AbstractFlowDTO;
import cloud.qasino.card.dto.event.EventOutput;
import cloud.qasino.card.entity.Game;
import cloud.qasino.card.entity.enums.game.Type;
import cloud.qasino.card.repositories.GameRepository;
import com.github.oxo42.stateless4j.StateMachineConfig;
import com.github.oxo42.stateless4j.StateRepresentation;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class AbstractController<T extends Game> implements Controller<T> {

    @Resource
    protected QasinoStateMachine stateMachine;
    protected T context;
    @Resource
    private GameRepository gameRepository;
    private List<String> endStates;

    @Override
    public T getContext() {
        return this.context;
    }

    @Override
    public void setContext(final T context) {
        this.context = context;
    }

    @Override
    public T init(final T context) {
        this.context = context;

        stateMachine.initialize(getStateMachineConfiguration());
        log.info(String.format("AbstractController init sets state: %s", stateMachine.getCurrentStateEnum()));

        context.setState(stateMachine.getCurrentStateEnum());

        // init a new default game has gameType HIGHLOW and gameVariant HRANNN with the initial state set in the state machine
        context.setType(Type.HIGHLOW);
        context.setStyle(Style.fromLabelWithDefault("").getLabel());
        this.setContext((T) gameRepository.save(this.getContext()));

        return this.context;
    }

    @Override
    public T init(final T context, final GameState currentState) {

        this.context = context;
        StateMachineConfig<GameState, GameTrigger> config = getStateMachineConfiguration();
        log.info(String.format("AbstractController init current state: %s", currentState));

        stateMachine.initialize(config, currentState);
        this.context.setState(stateMachine.getCurrentStateEnum());
        log.info(String.format("AbstractController init new state: %s", stateMachine.getCurrentStateEnum()));

        return this.context;
    }

    @Override
    public T reinstate(final int gameId) {

        this.setContext((T) gameRepository.findById(gameId).get());
        if (this.context == null) {
            throw new IllegalStateException("A resinstate was fired, but no game could be found to match it");
        }
        log.info(String.format("AbstractController reinstate current state: %s", this.context.getState()));

        return init(this.context, this.context.getState());
    }

    /**
     * Resets a state to a previous state.
     * <p/>
     * DO NOT OVERRIDE!!!!
     */
    public ControllerResponse reset() {
//		if (stateMachine.getSm().canFire(Trigger.RESET)) {
//			final ControllerResponse result = performRollback();
//			if (result.isSuccess()) {
//				transition(Trigger.RESET);
//			}
//			return result;
//		} else {
//			throw new IllegalStateException("Cannot reset from " + stateMachine.getCurrentState());
//		}
        return null;
    }

    /**
     * Protected method to perform a rollback of a step when the RESET is triggered for a specific state
     * This method doesn't do anything by default. Controllers can override this to perform any necessary rollbacks.
     *
     * @return response of the rollback (default is success)
     */
    protected ControllerResponse performRollback() {
        return ControllerResponse.success();
    }

    /**
     * Updates the controller state based on the event result
     *
     * @param state - State to move to
     */
    public T updateState(final String state) {
        log.info(String.format("AbstractController updateState before update: %s", state));

        this.getContext().setState(GameState.valueOf(state));

        // update the game with the new state
        this.setContext((T) gameRepository.save(this.getContext()));
        log.info(String.format("AbstractController updateState after update: %s", this.context.getState()));

        return this.context;

    }

    public void transition(final GameTrigger trigger) {
        log.info(String.format("AbstractController transition trigger is: %s", trigger));

        String nextState = this.stateMachine.transition(trigger);
        log.info(String.format("AbstractController transition next state: %s", nextState));
        this.updateState(nextState);

    }

    protected abstract StateMachineConfig<GameState, GameTrigger> getStateMachineConfiguration();

    protected List<String> getEndStates() {
        if (endStates == null) {
            StateMachineConfig<GameState, GameTrigger> config = this.getStateMachineConfiguration();
            endStates = new ArrayList<>();
            for (GameState state : GameState.values()) {
                StateRepresentation representation = config.getRepresentation(state);
                if (representation == null || representation.getPermittedTriggers().size() == 0) {
                    endStates.add(state.toString());
                }
            }
        }
        return endStates;
    }

    public boolean authorize(final GameState initialState, final AbstractFlowDTO dto) {
        stateMachine.check(initialState);
        dto.setStateMachine(this.stateMachine);
        final EventOutput result = dto.getNextInFlow().fireEvent(dto);
        if (result.getTrigger() != null) {
            transition(result.getTrigger());
        }
        return result.isSuccess();
    }
}
