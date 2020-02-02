package cloud.qasino.card.action;

import cloud.qasino.card.action.interfaces.Action;
import cloud.qasino.card.statemachine.GameState;
import cloud.qasino.card.entity.Game;
import cloud.qasino.card.event.EventOutput;
import cloud.qasino.card.repository.GameRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class DoSomethingWithGame implements Action<DoSomethingWithGame.DoSomethingWithGameActionDTO, EventOutput.Result> {

    @Resource
    private GameRepository gameRepository;

    @Override
    public EventOutput.Result perform(DoSomethingWithGameActionDTO dto) {
        log.debug("Do something with game");
        final Game currentGame = dto.getCurrentGame();
        return EventOutput.Result.SUCCESS;
    }

    public interface DoSomethingWithGameActionDTO {
        Game getCurrentGame();

        default GameState isPlaying() {
            return GameState.PLAYING;
        }
    }
}
