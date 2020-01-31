package cloud.qasino.card.action;

import cloud.qasino.card.controller.statemachine.GameState;
import cloud.qasino.card.dto.event.EventOutput;
import cloud.qasino.card.entity.Game;
import cloud.qasino.card.entity.enums.game.Type;
import cloud.qasino.card.orchestration.Action;
import cloud.qasino.card.repositories.GameRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class CancelOpenRequestsAction implements Action<CancelOpenRequestsAction.CancelOpenRequestsActionDTO, EventOutput.Result> {

    @Resource
    private GameRepository gameRepository;

    @Override
    public EventOutput.Result perform(CancelOpenRequestsActionDTO dto) {
        log.debug("Cancelling old requests which have not been authorized.");
        final Game currentGame = dto.getCurrentGame();
        final List<Game> requestToCancel = gameRepository
                .getCurrentGamesForUserByState(
                        currentGame,
                        dto.gameTypesToCancel(),
                        dto.getPendingStates()
                );

        return requestToCancel == null || requestToCancel.isEmpty()
                ? EventOutput.Result.SUCCESS
                : checkAndCancelRequests(dto, requestToCancel);
    }

    private EventOutput.Result checkAndCancelRequests(CancelOpenRequestsActionDTO dto, List<Game> gamesToConcel) {
        List<Game> gameToUpdate = new ArrayList<>();
        for (Game game : gamesToConcel) {
            if (!(dto.getCurrentGame().getGameId() == (game.getGameId()))) {
                gameToUpdate.add(game);
            }
        }

        if (!gameToUpdate.isEmpty()) {
            gameToUpdate.forEach(g -> g.setState(GameState.CANCELLED));
            log.debug("size list of requests pending for customer: " + gameToUpdate.size());
            gameRepository.saveAll(gameToUpdate);
        }
        return EventOutput.Result.SUCCESS;

    }

    public interface CancelOpenRequestsActionDTO {
        Game getCurrentGame();

        List<Type> gameTypesToCancel();

        Type getContextType();

        default List<GameState> getPendingStates() {
            return new ArrayList<>();
        }

        default GameState getStatesToCancelEventRequests() {
            return GameState.PLAYING;
        }
    }
}
