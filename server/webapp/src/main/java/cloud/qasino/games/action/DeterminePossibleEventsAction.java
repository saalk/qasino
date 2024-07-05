package cloud.qasino.games.action;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.GamingTable;
import cloud.qasino.games.database.repository.CardRepository;
import cloud.qasino.games.database.repository.GameRepository;
import cloud.qasino.games.database.repository.LeagueRepository;
import cloud.qasino.games.database.repository.PlayerRepository;
import cloud.qasino.games.database.repository.ResultsRepository;
import cloud.qasino.games.database.repository.PlayingRepository;
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
import static cloud.qasino.games.pattern.statemachine.event.PlayEvent.blackJackPossibleBotGamingTable;
import static cloud.qasino.games.pattern.statemachine.event.PlayEvent.blackJackPossibleHumanGamingTable;
import static cloud.qasino.games.pattern.statemachine.event.PlayEvent.highLowPossibleBotGamingTables;
import static cloud.qasino.games.pattern.statemachine.event.PlayEvent.highLowPossibleHumanGamingTables;


@Slf4j
@Component
public class DeterminePossibleEventsAction implements Action<DeterminePossibleEventsAction.Dto, EventOutput.Result> {

    @Resource
    GameRepository gameRepository;
    @Resource
    VisitorRepository visitorRepository;
    @Resource
    PlayerRepository playerRepository;
    @Resource
    CardRepository cardRepository;
    @Resource
    PlayingRepository playingRepository;
    @Resource
    LeagueRepository leagueRepository;
    @Resource
    ResultsRepository resultsRepository;

    @Override
    public EventOutput.Result perform(Dto actionDto) {

        List<PlayEvent> playEvents = new ArrayList<>();
        if (actionDto.getQasinoGame() == null) {
            playEvents = null;
        } else {
            switch (actionDto.getQasinoGame().getState().getGroup()) {
                case PLAYING -> {
                    switch (actionDto.getQasinoGame().getType()) {
                        case HIGHLOW -> {
                            if (actionDto.getGamingTablePlayer().isHuman()) {
                                playEvents = highLowPossibleHumanGamingTables;
                            } else {
                                playEvents = highLowPossibleBotGamingTables;
                            }
                        }
                        case BLACKJACK -> {
                            if (actionDto.getGamingTablePlayer().isHuman()) {
                                playEvents = blackJackPossibleHumanGamingTable;
                            } else {
                                playEvents = blackJackPossibleBotGamingTable;
                            }
                        }
                        default -> playEvents = null;
                    }
                }
                default -> playEvents = null;
            }
        }
        actionDto.setPossiblePlayEvents(playEvents);

        List<GameEvent> gameEvents = new ArrayList<>();
        if (actionDto.getQasinoGame() == null) {
            gameEvents = START_GAME_EVENTS;
        } else {
            switch (actionDto.getQasinoGame().getState().getGroup()) {
                case SETUP -> gameEvents = SETUP_GAME_EVENTS;
                case PREPARED -> gameEvents = PREPARED_GAME_EVENTS;
                case PLAYING -> gameEvents = PLAYING_GAME_EVENTS;
                case FINISHED -> gameEvents = null;
                default -> gameEvents = START_GAME_EVENTS;
            }
        }
        actionDto.setPossibleGameEvents(gameEvents);

        return EventOutput.Result.SUCCESS;
    }

    private void setNotFoundErrorMessage(Dto actionDto, String id, String value, String entity) {
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setNotFoundErrorMessage("[" + entity + "] not found for id [" + value + "]");
//        log.warn("Errors setNotFoundErrorMessage!!: {}", actionDto.getErrorMessage());

    }

    public interface Dto {

        // @formatter:off
        String getErrorMessage();
        GameEvent getSuppliedGameEvent();
        PlayEvent getSuppliedPlayEvent();

        // Getters
        Game getQasinoGame();
        GamingTable getActiveGamingTable();
        Player getGamingTablePlayer();

        // Setters
        void setPossiblePlayEvents(List<PlayEvent> playEvents);
        void setPossibleGameEvents(List<GameEvent> gameEvents);


        // error setters
        // @formatter:off
        void setBadRequestErrorMessage(String problem);
        void setNotFoundErrorMessage(String problem);
        void setConflictErrorMessage(String reason);
        void setUnprocessableErrorMessage(String reason);
        void setErrorKey(String errorKey);
        void setErrorValue(String errorValue);
        // @formatter:on
    }
}
