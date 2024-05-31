package cloud.qasino.games.action;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.Turn;
import cloud.qasino.games.database.repository.CardRepository;
import cloud.qasino.games.database.repository.GameRepository;
import cloud.qasino.games.database.repository.LeagueRepository;
import cloud.qasino.games.database.repository.PlayerRepository;
import cloud.qasino.games.database.repository.ResultsRepository;
import cloud.qasino.games.database.repository.TurnRepository;
import cloud.qasino.games.database.security.VisitorRepository;
import cloud.qasino.games.statemachine.event.EventOutput;
import cloud.qasino.games.statemachine.event.GameEvent;
import cloud.qasino.games.statemachine.event.TurnEvent;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static cloud.qasino.games.statemachine.event.GameEvent.PLAYING_GAME_EVENTS;
import static cloud.qasino.games.statemachine.event.GameEvent.PREPARED_GAME_EVENTS;
import static cloud.qasino.games.statemachine.event.GameEvent.SETUP_GAME_EVENTS;
import static cloud.qasino.games.statemachine.event.GameEvent.START_GAME_EVENTS;
import static cloud.qasino.games.statemachine.event.TurnEvent.blackJackPossibleBotTurn;
import static cloud.qasino.games.statemachine.event.TurnEvent.blackJackPossibleHumanTurn;
import static cloud.qasino.games.statemachine.event.TurnEvent.highLowPossibleBotTurns;
import static cloud.qasino.games.statemachine.event.TurnEvent.highLowPossibleHumanTurns;


@Slf4j
@Component
public class DeterminePossibleEvents implements Action<DeterminePossibleEvents.Dto, EventOutput.Result> {

    @Resource
    GameRepository gameRepository;
    @Resource
    VisitorRepository visitorRepository;
    @Resource
    PlayerRepository playerRepository;
    @Resource
    CardRepository cardRepository;
    @Resource
    TurnRepository turnRepository;
    @Resource
    LeagueRepository leagueRepository;
    @Resource
    ResultsRepository resultsRepository;

    @Override
    public EventOutput.Result perform(Dto actionDto) {

        List<TurnEvent> turnEvents = new ArrayList<>();
        if (actionDto.getQasinoGame() == null) {
            turnEvents = null;
        } else {
            switch (actionDto.getQasinoGame().getState().getGroup()) {
                case PLAYING -> {
                    switch (actionDto.getQasinoGame().getType()) {
                        case HIGHLOW -> {
                            if (actionDto.getTurnPlayer().isHuman()) {
                                turnEvents = highLowPossibleHumanTurns;
                            } else {
                                turnEvents = highLowPossibleBotTurns;
                            }
                        }
                        case BLACKJACK -> {
                            if (actionDto.getTurnPlayer().isHuman()) {
                                turnEvents = blackJackPossibleHumanTurn;
                            } else {
                                turnEvents = blackJackPossibleBotTurn;
                            }
                        }
                        default -> turnEvents = null;
                    }
                }
                default -> turnEvents = null;
            }
        }
        actionDto.setPossibleTurnEvents(turnEvents);

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

        // Getters
        Game getQasinoGame();
        Turn getActiveTurn();
        Player getTurnPlayer();

        // Setters
        void setPossibleTurnEvents(List<TurnEvent> turnEvents);
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
