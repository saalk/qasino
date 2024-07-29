package cloud.qasino.games.action.dto;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.Playing;
import cloud.qasino.games.database.repository.CardRepository;
import cloud.qasino.games.database.repository.GameRepository;
import cloud.qasino.games.database.repository.LeagueRepository;
import cloud.qasino.games.database.repository.PlayerRepository;
import cloud.qasino.games.database.repository.PlayingRepository;
import cloud.qasino.games.database.repository.ResultsRepository;
import cloud.qasino.games.database.security.VisitorRepository;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import cloud.qasino.games.pattern.statemachine.event.GameEvent;
import cloud.qasino.games.pattern.statemachine.event.PlayEvent;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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

        List<PlayEvent> playEvents = new ArrayList<>();
        if (qasino.getGame() == null) {
            playEvents = null;
        } else {
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
                        default -> playEvents = null;
                    }
                }
                default -> playEvents = null;
            }
        }
        qasino.getParams().setPossibleNextPlayEvents(playEvents);

        List<GameEvent> gameEvents = new ArrayList<>();
        if (qasino.getGame() == null) {
            gameEvents = START_GAME_EVENTS;
        } else {
            switch (qasino.getGame().getState().getGroup()) {
                case SETUP -> gameEvents = SETUP_GAME_EVENTS;
                case PREPARED -> gameEvents = PREPARED_GAME_EVENTS;
                case PLAYING -> gameEvents = PLAYING_GAME_EVENTS;
                case FINISHED -> gameEvents = null;
                default -> gameEvents = START_GAME_EVENTS;
            }
        }
        qasino.getParams().setPossibleNextGameEvents(gameEvents);
        return EventOutput.Result.SUCCESS;
    }
}
