package cloud.qasino.games.action;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.enums.game.GameState;
import cloud.qasino.games.database.repository.GameRepository;
import cloud.qasino.games.database.security.Visitor;
import cloud.qasino.games.database.service.GameServiceOld;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import cloud.qasino.games.pattern.statemachine.event.GameEvent;
import cloud.qasino.games.pattern.statemachine.event.PlayEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class UpdatePlayingStateForGame implements Action<UpdatePlayingStateForGame.Dto, EventOutput.Result> {

    @Autowired
    GameRepository gameRepository;

    @Autowired
    private GameServiceOld gameServiceOld;

    @Override
    public EventOutput.Result perform(Dto actionDto) {

        if (actionDto.getPlayingPlayer() == null) return EventOutput.Result.SUCCESS;
        switch (actionDto.getSuppliedPlayEvent()) {
            case PASS -> {
                // next player
                actionDto.setQasinoGame(updateGameState(gameServiceOld.findNextPlayerForGame(actionDto.getQasinoGame()), actionDto.getQasinoGame()));
            }
            case DEAL, HIGHER, LOWER, BOT -> {
                // existing player
                actionDto.setQasinoGame(updateGameState(actionDto.getPlayingPlayer(), actionDto.getQasinoGame()));
            }
        }
        return EventOutput.Result.SUCCESS;
    }

    private Game updateGameState(Player player, Game game) {
        if ((player.isHuman())) {
            if (game.getInitiator() == player.getPlayerId()) {
                if (game.getState() != GameState.INITIATOR_MOVE) {
                    game.setState(GameState.INITIATOR_MOVE);
                    return gameRepository.save(game);
                } else {
                    if (game.getState() != GameState.INVITEE_MOVE) {
                        game.setState(GameState.INVITEE_MOVE);
                        return gameRepository.save(game);
                    }
                }
            }
        } else {
            if (game.getState() != GameState.BOT_MOVE) {
                game.setState(GameState.BOT_MOVE);
                return gameRepository.save(game);
            }
        }
        return game;
    }


    public interface Dto {

        // @formatter:off
        String getErrorMessage();
        GameEvent getSuppliedGameEvent();
        PlayEvent getSuppliedPlayEvent();

        // Getters
        Visitor getQasinoVisitor();
        Game getQasinoGame();
        List<Player> getQasinoGamePlayers();
        Player getPlayingPlayer();

        // Setter
        void setQasinoGame(Game game);

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
