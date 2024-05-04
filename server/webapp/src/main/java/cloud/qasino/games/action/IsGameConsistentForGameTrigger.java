package cloud.qasino.games.action;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.Result;
import cloud.qasino.games.database.entity.Turn;
import cloud.qasino.games.database.entity.enums.game.gamestate.GameStateGroup;
import cloud.qasino.games.event.EventOutput;
import cloud.qasino.games.statemachine.trigger.GameTrigger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
/**
 * @param actionDto Validate game for supplied GameTrigger
 * 1) getSuppliedGameTrigger
 *
 * Business rules:
 * BR1)
 * @return Result.SUCCESS or FAILURE (404) when not found
 */
public class IsGameConsistentForGameTrigger implements Action<IsGameConsistentForGameTrigger.Dto, EventOutput.Result> {

    @Override
    public EventOutput.Result perform(Dto actionDto) {

        actionDto.setErrorKey("GameTrigger");
        actionDto.setErrorValue(actionDto.getSuppliedGameTrigger().getLabel());

        boolean noError = true;
        switch (actionDto.getSuppliedGameTrigger()) {

            case SETUP -> {
                // nothing to check as the game does not exist
            }
            case PREPARE -> {
                noError = gameShouldHaveStateInCorrectGameStateGroup(actionDto,
                        List.of(GameStateGroup.SETUP, GameStateGroup.PREPARED));
                noError = gameShouldHaveAnte(actionDto);
                if (noError) noError = gameShouldHaveInitiator(actionDto);
                if (noError) noError = gameShouldHavePlayersWithFiches(actionDto);
                if (noError) noError = playersShouldHaveSeats(actionDto);
            }
            case PLAY -> {
                noError = gameShouldHaveStateInCorrectGameStateGroup(actionDto, Collections.singletonList(GameStateGroup.PREPARED));
            }
            case TURN -> {
                noError = gameShouldHaveStateInCorrectGameStateGroup(actionDto, Collections.singletonList(GameStateGroup.PLAYING));
                if (noError) noError = gameShouldHaveCardsAndTurn(actionDto);
            }
            case STOP -> {
                noError = gameShouldHaveStateInCorrectGameStateGroup(actionDto, Collections.singletonList(GameStateGroup.PLAYING));
                if (noError) noError = gameShouldHaveCardsAndTurn(actionDto);
            }
            case WINNER -> {
                noError = gameShouldHaveAResult(actionDto);
                if (noError) noError = gameShouldHaveStateInCorrectGameStateGroup(actionDto, Collections.singletonList(GameStateGroup.PLAYING));
                if (noError) noError = gameShouldHaveCardsAndTurn(actionDto);
            }
        }
        return noError ? EventOutput.Result.SUCCESS : EventOutput.Result.FAILURE;
    }

    // @formatter:off
    // todo add AiLevel bot cannot be HUMAN
    private boolean gameShouldHavePlayers(Dto actionDto) {
        if (actionDto.getQasinoGamePlayers() == null) {
            log.info("!players");
            setUnprocessableErrorMessage(actionDto, "Action [" + actionDto.getSuppliedGameTrigger() + "] invalid - game has no players");
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
            log.info("!seats");
            setUnprocessableErrorMessage(actionDto, "Action [" + actionDto.getSuppliedGameTrigger() + "] invalid - no correct seat order for player(s)");
            return false;
        }
        return true;
    }
    private boolean gameShouldHaveStateInCorrectGameStateGroup(Dto actionDto, List<GameStateGroup> gameStateGroups) {
        if (!(gameStateGroups.contains(actionDto.getQasinoGame().getState().getGroup()))) {
            log.info("!state");
            setUnprocessableErrorMessage(actionDto,
                    "Action [" +
                            actionDto.getSuppliedGameTrigger() +
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
            log.info("!ante");
            setUnprocessableErrorMessage(actionDto,
                    "Action [" +
                            actionDto.getSuppliedGameTrigger() +
                            "] invalid - game has incorrect ante of " +
                            actionDto.getQasinoGame().getAnte()
            );
            return false;
        }
        return true;
    }
    private boolean gameShouldHaveInitiator(Dto actionDto) {
        if (actionDto.getQasinoGame().getInitiator() == 0) {
            log.info("!initiator");
            setUnprocessableErrorMessage(actionDto,
                    "Action [" +
                            actionDto.getSuppliedGameTrigger() +
                            "] invalid - game has no initiator "
            );
            return false;
        }
        return true;
    }
    private boolean gameShouldHavePlayersWithFiches(Dto actionDto) {
        for (Player player : actionDto.getQasinoGamePlayers()) {
            if (player.getFiches() == 0) {
                log.info("!fiches");
                setUnprocessableErrorMessage(actionDto,
                        "Action [" +
                                actionDto.getSuppliedGameTrigger() +
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
            log.info("!turn and/or cards");
            setUnprocessableErrorMessage(actionDto,
                    "Action [" +
                            actionDto.getSuppliedGameTrigger() +
                            "] invalid - game has no cards and or a turn"

            );
            return false;
        }
        return true;
    }
    private boolean gameShouldHaveAResult(Dto actionDto) {
        if (actionDto.getGameResults() == null) {
            log.info("!turn and/or cards");
            setUnprocessableErrorMessage(actionDto,
                    "Action [" +
                            actionDto.getSuppliedGameTrigger() +
                            "] invalid - game has no result"

            );
            return false;
        }
        return true;
    }
    // @formatter:on

    private void setUnprocessableErrorMessage(Dto actionDto, String reason) {
//        actionDto.setErrorKey(id); - already set
//        actionDto.setErrorValue(value); - already set
        actionDto.setUnprocessableErrorMessage(reason);
    }

    public interface Dto {

        // @formatter:off
        // Getters
        List<Player> getQasinoGamePlayers();
        GameTrigger getSuppliedGameTrigger();
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
