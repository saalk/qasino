package cloud.qasino.games.action;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.Result;
import cloud.qasino.games.database.entity.Turn;
import cloud.qasino.games.database.security.Visitor;
import cloud.qasino.games.database.entity.enums.game.gamestate.GameStateGroup;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import cloud.qasino.games.pattern.statemachine.event.GameEvent;
import cloud.qasino.games.pattern.statemachine.event.TurnEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
/**
 * @param actionDto Validate game for supplied GameEvent
 * 1) getSuppliedGameEvent
 *
 * Business rules:
 * BR1)
 * @return Result.SUCCESS or FAILURE (404) when not found
 */
public class IsGameConsistentForGameEventAction implements Action<IsGameConsistentForGameEventAction.Dto, EventOutput.Result> {

    @Override
    public EventOutput.Result perform(Dto actionDto) {

        actionDto.setErrorKey("GameEvent");
        actionDto.setErrorValue(actionDto.getSuppliedGameEvent().getLabel());

        boolean noError = true;
        switch (actionDto.getSuppliedGameEvent()) {

            case START -> {
                noError = noGameInSetupOrPlayingShouldAlreadyExist(actionDto);
            }
            case VALIDATE -> {
                noError = gameShouldHaveStateInCorrectGameStateGroup(actionDto,
                        List.of(GameStateGroup.SETUP, GameStateGroup.PREPARED));
                if (noError) noError = gameShouldHaveAnte(actionDto);
                if (noError) noError = gameShouldHaveInitiator(actionDto);
                if (noError) noError = gameShouldHavePlayersWithFiches(actionDto);
                if (noError) noError = playersShouldHaveSeats(actionDto);
            }
            case SHUFFLE -> {
                noError = gameShouldHaveStateInCorrectGameStateGroup(actionDto, Collections.singletonList(GameStateGroup.PREPARED));
            }
            case TURN -> {
                noError = gameShouldHaveStateInCorrectGameStateGroup(actionDto, Collections.singletonList(GameStateGroup.PLAYING));
                if (noError) noError = gameShouldHaveCardsAndTurn(actionDto);
            }
            case STOP -> {
//                noError = !gameShouldHaveStateInCorrectGameStateGroup(actionDto, Collections.singletonList(GameStateGroup.FINISHED));
            }
            case WINNER -> {
                noError = gameShouldHaveAResult(actionDto);
                if (noError) noError = gameShouldHaveStateInCorrectGameStateGroup(actionDto, Collections.singletonList(GameStateGroup.PLAYING));
                if (noError) noError = gameShouldHaveCardsAndTurn(actionDto);
            }
        }
//        log.warn("isGameConsistentForGameEvent noerror {}", noError);
        return noError ? EventOutput.Result.SUCCESS : EventOutput.Result.FAILURE;
    }

    // @formatter:off
    // todo add AiLevel bot cannot be HUMAN
    private boolean noGameInSetupOrPlayingShouldAlreadyExist(Dto actionDto) {

        List <Game> initiatedGame = actionDto.getInitiatedGamesForVisitor();
        List<String> reasonPart = new ArrayList<>();
        for (Game game : initiatedGame) {
            if (game.getState().getGroup() == GameStateGroup.SETUP) {
                reasonPart.add(GameStateGroup.SETUP.getLabel());
            } else if (game.getState().getGroup() == GameStateGroup.PREPARED) {
                reasonPart.add(GameStateGroup.PREPARED.getLabel());
            } else if (game.getState().getGroup() == GameStateGroup.PLAYING) {
                reasonPart.add(GameStateGroup.PLAYING.getLabel());
            }
        }
        if (!reasonPart.isEmpty()) {
            log.warn("!exists");
            setUnprocessableErrorMessage(actionDto,
                    "Action [" +
                            actionDto.getSuppliedGameEvent() +
                            "] is invalid - game already exists with game state group [" +
                            Arrays.toString(reasonPart.toArray()) + "]");
            return false;
        }
        return true;
    }
    private boolean playersShouldHaveSeats(Dto actionDto) {
        Optional<Player> player =
                actionDto.getQasinoGamePlayers()
                        .stream()
                        .filter(p -> p.getSeat() == 1)
                        .findFirst();
        if (player.isEmpty()) {
            log.warn("!seats");
            setUnprocessableErrorMessage(actionDto, "Action [" + actionDto.getSuppliedGameEvent() + "] invalid - no correct seat order for player(s)");
            return false;
        }
        return true;
    }
    private boolean gameShouldHaveStateInCorrectGameStateGroup(Dto actionDto, List<GameStateGroup> gameStateGroups) {
        if (!(gameStateGroups.contains(actionDto.getQasinoGame().getState().getGroup()))) {
            log.warn("!state");
            setUnprocessableErrorMessage(actionDto,
                    "Action [" +
                            actionDto.getSuppliedGameEvent() +
                            "] is invalid - game state [" +
                            actionDto.getQasinoGame().getState() +
                            "] is not in correct game state group [" +
                            Arrays.toString(gameStateGroups.toArray()) + "] which allows " +
                            GameStateGroup.listGameStatesForGameStateGroups(gameStateGroups));
            return false;
        }
        return true;
    }
    private boolean gameShouldHaveAnte(Dto actionDto) {
        if (actionDto.getQasinoGame().getAnte() <= 0) {
            log.warn("!ante");
            setUnprocessableErrorMessage(actionDto,
                    "Action [" +
                            actionDto.getSuppliedGameEvent() +
                            "] invalid - game has incorrect ante of " +
                            actionDto.getQasinoGame().getAnte()
            );
            return false;
        }
        return true;
    }
    private boolean gameShouldHaveInitiator(Dto actionDto) {
        if (actionDto.getQasinoGame().getInitiator() == 0) {
            log.warn("!initiator");
            setUnprocessableErrorMessage(actionDto,
                    "Action [" +
                            actionDto.getSuppliedGameEvent() +
                            "] invalid - game has no initiator "
            );
            return false;
        }
        return true;
    }
    private boolean gameShouldHavePlayersWithFiches(Dto actionDto) {
        for (Player player : actionDto.getQasinoGamePlayers()) {
            if (player.getFiches() == 0) {
                log.warn("!fiches");
                setUnprocessableErrorMessage(actionDto,
                        "Action [" +
                                actionDto.getSuppliedGameEvent() +
                                "] invalid - this player has no fiches " +
                                player.getPlayerId()

                );
                return false;
            }
        }
        return true;
    }
    private boolean gameShouldHaveCardsAndTurn(Dto actionDto) {
        if (actionDto.getActiveTurn() == null ||
                actionDto.getCardsInTheGameSorted() == null) {
            log.warn("!turn and/or cards");
            setUnprocessableErrorMessage(actionDto,
                    "Action [" +
                            actionDto.getSuppliedGameEvent() +
                            "] invalid - game has no cards and or a turn"

            );
            return false;
        }
        return true;
    }
    private boolean gameShouldHaveAResult(Dto actionDto) {
        if (actionDto.getGameResults() == null) {
            log.warn("!turn and/or cards");
            setUnprocessableErrorMessage(actionDto,
                    "Action [" +
                            actionDto.getSuppliedGameEvent() +
                            "] invalid - game has no result"

            );
            return false;
        }
        return true;
    }

    public EventOutput failure(Dto actionDto) {
        return new EventOutput(EventOutput.Result.FAILURE, actionDto.getSuppliedGameEvent(), actionDto.getSuppliedTurnEvent());
    }
    public EventOutput success(Dto actionDto) {
        return new EventOutput(EventOutput.Result.FAILURE, actionDto.getSuppliedGameEvent(), actionDto.getSuppliedTurnEvent());
    }

    // @formatter:on

    private void setUnprocessableErrorMessage(Dto actionDto, String reason) {
//        actionDto.setErrorKey(id); - already set
//        actionDto.setErrorValue(value); - already set
        actionDto.setUnprocessableErrorMessage(reason);
    }

    public interface Dto {

        // @formatter:off
        String getErrorMessage();
        GameEvent getSuppliedGameEvent();
        TurnEvent getSuppliedTurnEvent();

        // Getters
        List<Player> getQasinoGamePlayers();
        Visitor getQasinoVisitor();
        List<Game> getInitiatedGamesForVisitor();
        Game getQasinoGame();
        Turn getActiveTurn();
        List<Card> getCardsInTheGameSorted();
        List<Result> getGameResults();

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
