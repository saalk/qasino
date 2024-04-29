package cloud.qasino.games.action;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.Result;
import cloud.qasino.games.database.entity.Turn;
import cloud.qasino.games.database.entity.enums.game.GameState;
import cloud.qasino.games.database.entity.enums.game.gamestate.GameStateGroup;
import cloud.qasino.games.database.repository.VisitorRepository;
import cloud.qasino.games.event.EventOutput;
import cloud.qasino.games.statemachine.trigger.GameTrigger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static cloud.qasino.games.database.entity.enums.game.gamestate.GameStateGroup.listGameStatesForGameStateGroup;

@Slf4j
@Component
public class IsGameConsistentForGameTrigger implements Action<IsGameConsistentForGameTrigger.Dto, EventOutput.Result> {

    @Override
    public EventOutput.Result perform(Dto actionDto) {

        actionDto.setErrorKey("GameTrigger");
        actionDto.setErrorValue(actionDto.getSuppliedGameTrigger().getLabel());
        boolean noError = true;
        switch (actionDto.getSuppliedGameTrigger()) {

            case WINNER -> {
                noError = gameShouldHaveAResult(actionDto);
                if (noError) noError = gameShouldHaveStateInCorrectGameStateGroup(actionDto, GameStateGroup.STARTED);
                if (noError) noError = gameShouldHaveCardsAndTurn(actionDto);
                break;
            }
            case PLAY -> {
                noError = gameShouldHaveStateInCorrectGameStateGroup(actionDto, GameStateGroup.PREPARED);
                break;
            }
            case TURN -> {
                noError = gameShouldHaveStateInCorrectGameStateGroup(actionDto, GameStateGroup.STARTED);
                if (noError) noError = gameShouldHaveCardsAndTurn(actionDto);
                break;
            }
            case LEAVE -> {
                noError = gameShouldHaveStateInCorrectGameStateGroup(actionDto, GameStateGroup.STARTED);
                if (noError) noError = gameShouldHaveCardsAndTurn(actionDto);
                break;
            }
            case PREPARE -> {
                noError = gameShouldHaveAnte(actionDto);
                if (noError) noError = gameShouldHaveInitiator(actionDto);
                if (noError) noError = gameShouldHavePlayersWithFiches(actionDto);
                if (noError) noError = playersShouldHaveSeats(actionDto);
                break;
            }
            case SETUP -> {
                noError = gameShouldHaveStateInCorrectGameStateGroup(actionDto, GameStateGroup.SETUP);
                break;
            }
        }
        return noError ? EventOutput.Result.SUCCESS : EventOutput.Result.FAILURE;
    }

    private boolean gameShouldHavePlayers(Dto actionDto) {
        if (actionDto.getQasinoGamePlayers() == null) {
            log.info("!players");
            actionDto.setHttpStatus(422);
            actionDto.setErrorMessage("Action [" + actionDto.getSuppliedGameTrigger() + "] invalid - game has no players");
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
            actionDto.setHttpStatus(422);
            actionDto.setErrorMessage("Action [" + actionDto.getSuppliedGameTrigger() + "] invalid - no correct seat order for player(s)");
            return false;
        }
        return true;
    }
    private boolean gameShouldHaveStateInCorrectGameStateGroup(Dto actionDto, GameStateGroup gameStateGroup) {
        Set<GameState> correctGameStates = listGameStatesForGameStateGroup(gameStateGroup);
        log.info("correctGameStates: " + correctGameStates);
        if (!(correctGameStates.contains(actionDto.getQasinoGame().getState()))) {
            log.info("!state");
            actionDto.setHttpStatus(422);
            actionDto.setErrorMessage(
                    "Action [" +
                            actionDto.getSuppliedGameTrigger() +
                            "] is invalid - game state [" + actionDto.getQasinoGame().getState() + "] is not in correct game state group [" +
                            gameStateGroup.getLabel() + "] which contains " + correctGameStates
            );
            return false;
        }
        return true;
    }
    private boolean gameShouldHaveAnte(Dto actionDto) {
        if (actionDto.getQasinoGame().getAnte() <= 0) {
            log.info("!ante");
            actionDto.setHttpStatus(422);
            actionDto.setErrorMessage(
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
            actionDto.setHttpStatus(422);
            actionDto.setErrorMessage(
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
                actionDto.setHttpStatus(422);
                actionDto.setErrorMessage(
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
            actionDto.setHttpStatus(422);
            actionDto.setErrorMessage(
                    "Action [" +
                            actionDto.getSuppliedGameTrigger() +
                            "] invalid - game has no cards and or a turn"

            );
            return false;
        }
        return true;
    }
    private boolean gameShouldHaveAResult(Dto actionDto) {
        if (actionDto.getGameResult() == null) {
            log.info("!turn and/or cards");
            actionDto.setHttpStatus(422);
            actionDto.setErrorMessage(
                    "Action [" +
                            actionDto.getSuppliedGameTrigger() +
                            "] invalid - game has no result"

            );
            return false;
        }
        return true;
    }

    public interface Dto {

        // @formatter:off
        // Getters
        List<Player> getQasinoGamePlayers();
        GameTrigger getSuppliedGameTrigger();
        Game getQasinoGame();
        Turn getActiveTurn();
        List<Card> getCardsInTheGameSorted();
        Result getGameResult();

        // error setters and getters
        void setHttpStatus(int status);
        int getHttpStatus();
        void setErrorKey(String errorKey);
        String getErrorKey();
        void setErrorValue(String errorValue);
        String getErrorValue();
        void setErrorMessage(String message);
        // @formatter:on
    }
}
