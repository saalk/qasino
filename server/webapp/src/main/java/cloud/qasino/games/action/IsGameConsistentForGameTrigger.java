package cloud.qasino.games.action;

import cloud.qasino.games.action.interfaces.Action;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.enums.game.GameState;
import cloud.qasino.games.database.repository.VisitorRepository;
import cloud.qasino.games.event.EventOutput;
import cloud.qasino.games.statemachine.trigger.GameTrigger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class IsGameConsistentForGameTrigger implements Action<IsGameConsistentForGameTrigger.Dto, EventOutput.Result> {

    @Resource
    VisitorRepository visitorRepository;

    @Override
    public EventOutput.Result perform(Dto actionDto) {

        actionDto.setErrorKey("GameTrigger");
        actionDto.setErrorValue(actionDto.getSuppliedGameTrigger().getLabel());

        switch (actionDto.getSuppliedGameTrigger()) {

        }
//        EventOutput.Result failure = gameShouldHavePlayers(actionDto);
//        if (failure != null) return failure;
        int size = actionDto.getQasinoGamePlayers().size();
        List<Integer> order = actionDto.getQasinoGame().getSeats();

        Optional<Player> player =
                actionDto.getQasinoGamePlayers()
                        .stream()
                        .filter(p -> p.getSeat() == 1)
                        .findFirst();
        if (player.isEmpty()) {
            log.info("!seats");
            actionDto.setHttpStatus(422);
            actionDto.setErrorMessage("Action [" + actionDto.getSuppliedGameTrigger() + "] invalid - game no correct seat list for player(s)");
            return EventOutput.Result.FAILURE;
        }


        if (actionDto.getQasinoGame().getState() != GameState.PREPARED) {
            log.info("!state");
            actionDto.setHttpStatus(422);
            actionDto.setErrorMessage("Action [" + actionDto.getSuppliedGameTrigger() + "] invalid - game is not in prepared state");
            return EventOutput.Result.FAILURE;
        }
        return EventOutput.Result.SUCCESS;
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

    public interface Dto {

        // @formatter:off
        // Getters
        List<Player> getQasinoGamePlayers();
        GameTrigger getSuppliedGameTrigger();
        Game getQasinoGame();

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
