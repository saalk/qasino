package cloud.qasino.card.components.retry;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Lazy
@Getter
@Setter
@Slf4j
public class QasinoRequestRetryConfig {

    public static final String PARAM_RETRY_ON_ERROR = "param/service/timeout/retry-on-error";
    public static final String PARAM_RESET_TO_PREVIOUS_STATE = "param/service/timeout/reset-to-previous-state";
    public static final String PARAM_RETRY_INTERVAL = "param/service/timeout/retry-interval-seconds";
    public static final String PARAM_MAX_RETRIES = "param/service/timeout/retry-max";
    public static final String PARAM_SCHEDULER_INTERVAL = "param/service/timeout/scheduler-interval-seconds";

    private boolean retryOnError;

    private int retryIntervalSeconds;

    private int maxRetrials;

    private int schedulerIntervalSeconds;

    private boolean resetToPreviousState;

/*
    @Resource
    private JNDIUtil jndiUtil;
*/

    @PostConstruct
    public void init() {
 /*       retryOnError = jndiUtil.getJndiValueWithDefault(PARAM_RETRY_ON_ERROR, false);
        resetToPreviousState = jndiUtil.getJndiValueWithDefault(PARAM_RESET_TO_PREVIOUS_STATE,false);
        retryIntervalSeconds = jndiUtil.getJndiValueWithDefault(PARAM_RETRY_INTERVAL,900);
        maxRetrials = jndiUtil.getJndiValueWithDefault(PARAM_MAX_RETRIES,10);
        schedulerIntervalSeconds = jndiUtil.getJndiValueWithDefault(PARAM_SCHEDULER_INTERVAL,30);
 */
        log.debug("Retry properties, retryIntervalSeconds: {}, maxNrRetrials: {}, schedulerIntervalSeconds: {}",
                retryIntervalSeconds, maxRetrials, schedulerIntervalSeconds);
    }

}
