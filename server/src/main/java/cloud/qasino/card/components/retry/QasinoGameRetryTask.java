package cloud.qasino.card.components.retry;

import cloud.qasino.card.controller.statemachine.GameState;
import cloud.qasino.card.entity.Game;
import cloud.qasino.card.repositories.GameRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Optional;

@Component
@Lazy
@Slf4j
public class QasinoGameRetryTask implements RetryTask<Game> {

    @Resource
    private GraphiteRetryHelper graphiteRetryHelper;

    @Resource
    private RetryController retryController;

    @Resource
    private GameRepository gameRepository;

    @Override
    public boolean retry(Game input) {
        if (input == null) {
            log.error("Cannot retry null game");
            return false;
        }
        log.warn("Retrying gameId={}", input.getGameId());
        boolean retried = false;
        try {
            retried = retryController.handleRetry(input);
        } catch (Exception e) {
            Optional<Game> optionalGame = gameRepository.findById(input.getGameId());
            Game game = optionalGame.get();
            if (game.getState() != GameState.ERROR) {
                log.warn("Setting gameId={} to ERROR state after retry", input.getGameId());
                game.setPreviousState(game.getState());
                game.setState(GameState.ERROR);
                gameRepository.save(game);
            }
            throw e;
        }
        // update graphite with retry count
        graphiteRetryHelper.tickRetriedGamesCounter();
        return retried;
    }

    @Override
    public void giveUp(Game game) {

        graphiteRetryHelper.tickTimedOutGamesCounter();
    }

}
