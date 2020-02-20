package applyextra.commons.components.retry;

import applyextra.commons.helper.GraphiteHelper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Lazy
public class GraphiteRetryHelper {
    private static final String GRAPHITE_COUNTER_PREFIX = "retry";
    private static final int TICK_VALUE = 1;
    private static final String RETRIED_REQUESTS_COUNTER = "requests_retried";
    private static final String PENDING_REQUESTS_COUNTER = "requests_pending";
    private static final String TIMED_OUT_REQUESTS_COUNTER = "requests_timed_out_after_retry";

    @Resource
    private GraphiteHelper graphiteHelper;

    public void tickRetriedRequestsCounter(){
        graphiteHelper.customCounter(GRAPHITE_COUNTER_PREFIX, RETRIED_REQUESTS_COUNTER, TICK_VALUE);
    }

    public void tickPendingRequestsCounter(){
        graphiteHelper.customCounter(GRAPHITE_COUNTER_PREFIX, PENDING_REQUESTS_COUNTER, TICK_VALUE);
    }

    public void tickTimedOutRequestsCounter(){
        graphiteHelper.customCounter(GRAPHITE_COUNTER_PREFIX, TIMED_OUT_REQUESTS_COUNTER, TICK_VALUE);
    }
}
