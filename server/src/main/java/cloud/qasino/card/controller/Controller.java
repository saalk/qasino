package cloud.qasino.card.controller;

import cloud.qasino.card.controller.statemachine.GameState;
import cloud.qasino.card.controller.statemachine.GameTrigger;
import cloud.qasino.card.entity.Game;

public interface Controller<T extends Game> {

    T init(T context, GameState currentState);

    T init(T context);

    T reinstate(final int id);

    T getContext();

    void setContext(T context);

    void transition(final GameTrigger trigger);

    ControllerResponse reset();
}
