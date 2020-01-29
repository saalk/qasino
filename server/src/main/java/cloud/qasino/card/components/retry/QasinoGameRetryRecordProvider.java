package cloud.qasino.card.components.retry;

import cloud.qasino.card.entity.Game;
import cloud.qasino.card.repositories.GameRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;

import static cloud.qasino.card.controller.statemachine.GameState.TIMEOUT;

@Component
@Lazy
public class QasinoGameRetryRecordProvider implements RetryRecordProvider<Game> {

    @Lazy
    @Resource
    private GameRepository gameRepository;

    @Resource
    private GraphiteRetryHelper graphiteRetryHelper;

    @Resource
    @Lazy
    private RetryController retryController;

    @Resource
    private QasinoRequestRetryConfig retryConfig;

    private final Map<String, RetryRecord<Game>> retryRecordsByRequestId = new HashMap();

    @Override
    public List<RetryRecord<Game>> getRetryRecords() {
        final List<RetryRecord<Game>> result = new ArrayList<>();
        for (RetryCriteria criteria : retryController.getRetryCriteria()) {
            if (criteria.getTypeCriteria() == null) {
                continue;
            }
            result.addAll(toRetryRecords(loadPendingRequests(criteria)));
            result.addAll(toRetryRecords(loadRequestsInErrorState(criteria)));
        }
        return result;
    }

    private List<Game> loadPendingRequests(final RetryCriteria criteria) {
        if (criteria.getPendingRequestStateCriteria().isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        return gameRepository.findAll();
/*                .findRequestsPendingByRequestTypeAndCurrentStates( // load from db all requests
                        criteria.getRequestTypeCriteria(),          // with current state in
                        criteria.getPendingRequestStateCriteria()   // <- this list
                );
*/
    }

    private List<Game> loadRequestsInErrorState(final RetryCriteria criteria) {
        if (criteria.getErrorAndPreviousStateCriteria().isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        return gameRepository.findAll();
/*                .findRequestsInErrorStateByRequestTypeAndPreviousState( // load from db all requests with current
                        criteria.getRequestTypeCriteria(),          // state error and previous state in
                        criteria.getErrorAndPreviousStateCriteria() // <- this list
                );*/
    }

    @Override
    public void removeRetryRecord(Game input) {
        RetryRecord<Game> retryRecord = retryRecordsByRequestId.get(input.getGameId());
        if (retryRecord.getNrOfRetriesAttempted() < retryConfig.getMaxRetrials()) {
            return;
        }

        input.setState(TIMEOUT);
        gameRepository.save(input);
        retryRecordsByRequestId.remove(input);
    }

    @Override
    public void updateRetryRecord(Game entity) {
        if (retryConfig.isResetToPreviousState()) {
            entity.setState(entity.getPreviousState());
            gameRepository.save(entity);
        }
    }

    private List<RetryRecord<Game>> toRetryRecords(
            final List<Game> pendingGames
    ) {
        final List<RetryRecord<Game>> result = new ArrayList<>();
        final LocalDateTime twoDaysAgo = LocalDateTime.now().minusDays(2);
        // .withTime(0,0,0,0);
 /*       for (final Game game : pendingGames) {
            final LocalDateTime updatedTime = new LocalDateTime(game.getUpdateTime());
            if (updatedTime.isBefore(twoDaysAgo)){
                continue;
            }
            if (retryRecordsByRequestId.containsKey(game.getId())) {
                final RetryRecord<Game> retryRecord = retryRecordsByRequestId.get(game.getId());
                retryRecord.setRetryInput(game); // update input
                result.add(retryRecord);
            } else {
                // first retry, create new retry record
                final RetryRecord<Game> newRetryRecord = new RetryRecord<>
                        (game, game.getUpdateTime(), 0);
                retryRecordsByRequestId.put(game.getId(), newRetryRecord);
                graphiteRetryHelper.tickPendingRequestsCounter();
                newRetryRecord.setRetryInput(game);
                result.add(newRetryRecord);
            }
        }*/
        return result;
    }

}
