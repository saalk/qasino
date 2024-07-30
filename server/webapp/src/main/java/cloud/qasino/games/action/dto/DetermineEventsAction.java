package cloud.qasino.games.action.dto;

import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import cloud.qasino.games.pattern.statemachine.event.GameEvent;
import cloud.qasino.games.pattern.statemachine.event.PlayEvent;
import cloud.qasino.games.pattern.statemachine.event.QasinoEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static cloud.qasino.games.pattern.statemachine.event.GameEvent.PLAYING_GAME_EVENTS;
import static cloud.qasino.games.pattern.statemachine.event.GameEvent.PREPARED_GAME_EVENTS;
import static cloud.qasino.games.pattern.statemachine.event.GameEvent.SETUP_GAME_EVENTS;
import static cloud.qasino.games.pattern.statemachine.event.GameEvent.START_GAME_EVENTS;
import static cloud.qasino.games.pattern.statemachine.event.PlayEvent.blackJackPossibleBotPlaying;
import static cloud.qasino.games.pattern.statemachine.event.PlayEvent.blackJackPossibleHumanPlaying;
import static cloud.qasino.games.pattern.statemachine.event.PlayEvent.highLowPossibleBotPlayings;
import static cloud.qasino.games.pattern.statemachine.event.PlayEvent.highLowPossibleHumanPlayings;


@Slf4j
@Component
public class DetermineEventsAction extends ActionDto<EventOutput.Result> {

    @Override
    public EventOutput.Result perform(Qasino qasino) {

        List<QasinoEvent> qasinoEvents = getQasinoEvents(qasino);
        qasino.getParams().setPossibleNextQasinoEvents(qasinoEvents);

        List<PlayEvent> playEvents = getPlayEvents(qasino);
        qasino.getParams().setPossibleNextPlayEvents(playEvents);

        List<GameEvent> gameEvents = getGameEvents(qasino);
        qasino.getParams().setPossibleNextGameEvents(gameEvents);

        return EventOutput.Result.SUCCESS;
    }

    // @formatter:off
    private static List<QasinoEvent> getQasinoEvents(Qasino qasino) {
        if (qasino.getVisitor() == null) return Collections.singletonList(QasinoEvent.LOGON);
        if (qasino.getVisitor().getSecuredLoan() == 0) return Collections.singletonList(QasinoEvent.PAWN);
        if (qasino.getVisitor().getBalance() >= qasino.getVisitor().getSecuredLoan()) {
            return Collections.singletonList(QasinoEvent.REPAY);
        } else {
            return Collections.singletonList(QasinoEvent.NONE);
        }
    }
    private static List<GameEvent> getGameEvents(Qasino qasino) {
        List<GameEvent> gameEvents;
        if (qasino.getVisitor() == null) return Collections.singletonList(GameEvent.NONE);
        if (qasino.getGame() != null) {
            switch (qasino.getGame().getState().getGroup()) {
                case SETUP -> gameEvents = SETUP_GAME_EVENTS;
                case PREPARED -> gameEvents = PREPARED_GAME_EVENTS;
                case PLAYING -> gameEvents = PLAYING_GAME_EVENTS;
                case FINISHED -> gameEvents = START_GAME_EVENTS;
                default -> gameEvents = Collections.singletonList(GameEvent.ERROR);
            }
        } else {
            gameEvents = START_GAME_EVENTS;
        }
        return gameEvents;
    }
    private static List<PlayEvent> getPlayEvents(Qasino qasino) {
        List<PlayEvent> playEvents = new ArrayList<>();
        if (qasino.getVisitor() == null) return Collections.singletonList(PlayEvent.NONE);
        if (qasino.getGame() != null) {
            switch (qasino.getGame().getState().getGroup()) {
                case PLAYING -> {
                    switch (qasino.getGame().getType()) {
                        case HIGHLOW -> {
                            if (qasino.getPlaying().getCurrentPlayer().isHuman()) {
                                playEvents = highLowPossibleHumanPlayings;
                            } else {
                                playEvents = highLowPossibleBotPlayings;
                            }
                        }
                        case BLACKJACK -> {
                            if (qasino.getPlaying().getCurrentPlayer().isHuman()) {
                                playEvents = blackJackPossibleHumanPlaying;
                            } else {
                                playEvents = blackJackPossibleBotPlaying;
                            }
                        }
                        default -> playEvents = Collections.singletonList(PlayEvent.NONE);
                    }
                }
                default -> playEvents = Collections.singletonList(PlayEvent.NONE);
            }
        } else {
            playEvents = Collections.singletonList(PlayEvent.NONE);
        }
        return playEvents;
    }
}
