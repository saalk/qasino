package cloud.qasino.games.action.game;

import cloud.qasino.games.action.common.GenericLookupsAction;
import cloud.qasino.games.database.entity.enums.game.gamestate.GameStateGroup;
import cloud.qasino.games.dto.Qasino;
import cloud.qasino.games.dto.model.PlayerDto;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
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
public class IsGameConsistentForGameEventAction extends GenericLookupsAction<EventOutput.Result> {

    @Override
    public EventOutput.Result perform(Qasino qasino) {

        qasino.getMessage().setErrorKey("GameEvent");
        qasino.getMessage().setErrorValue(qasino.getParams().getSuppliedGameEvent().getLabel());

        boolean noError = true;
        switch (qasino.getParams().getSuppliedGameEvent()) {

            case START -> {
                noError = noGameInSetupOrPlayingShouldAlreadyExist(qasino);
            }
            case VALIDATE -> {
                noError = gameShouldHaveStateInCorrectGameStateGroup(qasino,
                        List.of(GameStateGroup.SETUP, GameStateGroup.PREPARED));
                if (noError) noError = gameShouldHaveVisitorWithBalance(qasino);
                if (noError) noError = gameShouldHaveAnte(qasino);
                if (noError) noError = gameShouldHaveInitiator(qasino);
                if (noError) noError = gameShouldHavePlayersWithFiches(qasino);
                if (noError) noError = playersShouldHaveSeats(qasino);
                if (noError) noError = playersShouldHaveSeats(qasino);
                if (noError) noError = gameShouldNotHaveCardsOrPlaying(qasino);

            }
            case SHUFFLE -> {
                noError = gameShouldHaveStateInCorrectGameStateGroup(qasino, Collections.singletonList(GameStateGroup.PREPARED));
                if (noError) noError = gameShouldNotHaveCardsOrPlaying(qasino);
            }
            case PLAY -> {
                noError = gameShouldHaveStateInCorrectGameStateGroup(qasino, Collections.singletonList(GameStateGroup.PLAYING));
                if (noError) noError = gameShouldHaveCardsAndPlaying(qasino);
            }
            case STOP -> {
//                noError = !gameShouldHaveStateInCorrectGameStateGroup(qasino, Collections.singletonList(GameStateGroup.FINISHED));
            }
            case WINNER -> {
                noError = gameShouldHaveAResult(qasino);
                if (noError)
                    noError = gameShouldHaveStateInCorrectGameStateGroup(qasino, Collections.singletonList(GameStateGroup.PLAYING));
                if (noError) noError = gameShouldHaveCardsAndPlaying(qasino);
            }
        }
//        log.warn("isGameConsistentForGameEvent noerror {}", noError);
        return noError ? EventOutput.Result.SUCCESS : EventOutput.Result.FAILURE;
    }

    // @formatter:off
    // todo add AiLevel bot cannot be HUMAN
    private boolean noGameInSetupOrPlayingShouldAlreadyExist(Qasino qasino) {

        List<String> reasonPart = new ArrayList<>();
        if (qasino.getGame().getState().getGroup() == GameStateGroup.SETUP) {
            reasonPart.add(GameStateGroup.SETUP.getLabel());
        } else if (qasino.getGame().getState().getGroup() == GameStateGroup.PREPARED) {
            reasonPart.add(GameStateGroup.PREPARED.getLabel());
        } else if (qasino.getGame().getState().getGroup() == GameStateGroup.PLAYING) {
            reasonPart.add(GameStateGroup.PLAYING.getLabel());
        }
        if (!reasonPart.isEmpty()) {
            qasino.getMessage().setUnprocessableErrorMessage(
                    "GameEvent",
                    qasino.getParams().getSuppliedGameEvent().getLabel(),
                    "Action [" +
                            qasino.getParams().getSuppliedGameEvent() +
                            "] is invalid - game already exists with game state group [" +
                            Arrays.toString(reasonPart.toArray()) + "]");
            return false;
        }
        return true;
    }
    private boolean playersShouldHaveSeats(Qasino qasino) {
        Optional<PlayerDto> player =
                qasino.getGame().getPlayers()
                        .stream()
                        .filter(p -> p.getSeat() == 1)
                        .findFirst();
        if (player.isEmpty()) {
            qasino.getMessage().setUnprocessableErrorMessage(
                    "GameEvent",
                    qasino.getParams().getSuppliedGameEvent().getLabel(),
            "Action [" + qasino.getParams().getSuppliedGameEvent() + "] invalid - no correct seat order for player(s)");
            return false;
        }
        return true;
    }
    private boolean gameShouldHaveStateInCorrectGameStateGroup(Qasino qasino, List<GameStateGroup> gameStateGroups) {
        if (!(gameStateGroups.contains(qasino.getGame().getState().getGroup()))) {
            qasino.getMessage().setUnprocessableErrorMessage(
                    "GameEvent",
                    qasino.getParams().getSuppliedGameEvent().getLabel(),
                    "Action [" +
                            qasino.getParams().getSuppliedGameEvent() +
                            "] is invalid - game state [" +
                            qasino.getGame().getState() +
                            "] is not in correct game state group [" +
                            Arrays.toString(gameStateGroups.toArray()) + "] which allows " +
                            GameStateGroup.listGameStatesForGameStateGroups(gameStateGroups));
            return false;
        }
        return true;
    }
    private boolean gameShouldHaveAnte(Qasino qasino) {
        if (qasino.getGame().getAnte() <= 0) {
            qasino.getMessage().setUnprocessableErrorMessage(
                    "GameEvent",
                    qasino.getParams().getSuppliedGameEvent().getLabel(),
                    "Action [" +
                            qasino.getParams().getSuppliedGameEvent() +
                            "] invalid - game has incorrect ante of " +
                            qasino.getGame().getAnte()
            );
            return false;
        }
        return true;
    }
    private boolean gameShouldHaveVisitorWithBalance(Qasino qasino) {
        if (qasino.getGame().getInitiator() == 0) {
            qasino.getMessage().setUnprocessableErrorMessage(
                    "GameEvent",
                    qasino.getParams().getSuppliedGameEvent().getLabel(),
                    "Action [" +
                            qasino.getParams().getSuppliedGameEvent() +
                            "] invalid - game has no initiator "
            );
            return false;
        }
        if (qasino.getVisitor().getBalance() == 0) {
            qasino.getMessage().setUnprocessableErrorMessage(
                    "GameEvent",
                    qasino.getParams().getSuppliedGameEvent().getLabel(),
                    "Action [" +
                            qasino.getParams().getSuppliedGameEvent() +
                            "] invalid - initiator has no balance "
            );
            return false;
        }
        return true;
    }
    private boolean gameShouldHaveInitiator(Qasino qasino) {
        if (qasino.getGame().getInitiator() == 0) {
            qasino.getMessage().setUnprocessableErrorMessage(
                    "GameEvent",
                    qasino.getParams().getSuppliedGameEvent().getLabel(),
                    "Action [" +
                            qasino.getParams().getSuppliedGameEvent() +
                            "] invalid - game has no initiator "
            );
            return false;
        }
        return true;
    }
    private boolean gameShouldHavePlayersWithFiches(Qasino qasino) {
        for (PlayerDto player : qasino.getGame().getPlayers()) {
            if (player.getFiches() == 0) {
                qasino.getMessage().setUnprocessableErrorMessage(
                        "GameEvent",
                        qasino.getParams().getSuppliedGameEvent().getLabel(),
                        "Action [" +
                                qasino.getParams().getSuppliedGameEvent() +
                                "] invalid - this player has no fiches " +
                                player.getPlayerId()

                );
                return false;
            }
        }
        return true;
    }
    private boolean gameShouldHaveCardsAndPlaying(Qasino qasino) {
        if (qasino.getPlaying() == null ||
                qasino.getGame().getCardsInStock().isEmpty()) {
            qasino.getMessage().setUnprocessableErrorMessage(
                    "GameEvent",
                    qasino.getParams().getSuppliedGameEvent().getLabel(),
                    "Action [" +
                            qasino.getParams().getSuppliedGameEvent() +
                            "] invalid - game has no cards and or a playing"

            );
            return false;
        }
        return true;
    }
    private boolean gameShouldNotHaveCardsOrPlaying(Qasino qasino) {
        if (qasino.getPlaying() != null) {
            qasino.getMessage().setUnprocessableErrorMessage(
                    "GameEvent",
                    qasino.getParams().getSuppliedGameEvent().getLabel(),
                    "Action [" +
                            qasino.getParams().getSuppliedGameEvent() +
                            "] invalid - game already has playing"

            );
            return false;
        }
        if (!qasino.getGame().getCards().isEmpty()) {
            qasino.getMessage().setUnprocessableErrorMessage(
                    "GameEvent",
                    qasino.getParams().getSuppliedGameEvent().getLabel(),
                    "Action [" +
                            qasino.getParams().getSuppliedGameEvent() +
                            "] invalid - game already has cards"

            );
            return false;
        }
        return true;
    }
    private boolean gameShouldHaveAResult(Qasino qasino) {
        if (qasino.getResults().isEmpty()) {
            qasino.getMessage().setUnprocessableErrorMessage(
                    "GameEvent",
                    qasino.getParams().getSuppliedGameEvent().getLabel(),
                    "Action [" +
                            qasino.getParams().getSuppliedGameEvent() +
                            "] invalid - game has no result"

            );
            return false;
        }
        return true;
    }

    public EventOutput failure(Qasino qasino) {
        return new EventOutput(EventOutput.Result.FAILURE, qasino.getParams().getSuppliedGameEvent(), qasino.getParams().getSuppliedPlayEvent());
    }
    public EventOutput success(Qasino qasino) {
        return new EventOutput(EventOutput.Result.FAILURE, qasino.getParams().getSuppliedGameEvent(), qasino.getParams().getSuppliedPlayEvent());
    }
}
