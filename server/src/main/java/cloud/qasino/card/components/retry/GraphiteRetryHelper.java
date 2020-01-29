package cloud.qasino.card.components.retry;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Lazy
public class GraphiteRetryHelper {
    private static final String GRAPHITE_COUNTER_PREFIX = "retry";
    private static final int TICK_VALUE = 1;
    private static final String RETRIED_REQUESTS_COUNTER = "requests_retried";
    private static final String PENDING_REQUESTS_COUNTER = "requests_pending";
    private static final String TIMED_OUT_REQUESTS_COUNTER = "requests_timed_out_after_retry";

//    @Resource
//    private GraphiteHelper graphiteHelper;

    public void tickRetriedGamesCounter() {
//        graphiteHelper.customCounter(GRAPHITE_COUNTER_PREFIX, RETRIED_GAMES_COUNTER, TICK_VALUE);
    }

    public void tickPendingGamesCounter() {
//        graphiteHelper.customCounter(GRAPHITE_COUNTER_PREFIX, PENDING_GAMES_COUNTER, TICK_VALUE);
    }

    public void tickTimedOutGamesCounter() {
//        graphiteHelper.customCounter(GRAPHITE_COUNTER_PREFIX, TIMED_OUT_GAMES_COUNTER, TICK_VALUE);
    }
}
