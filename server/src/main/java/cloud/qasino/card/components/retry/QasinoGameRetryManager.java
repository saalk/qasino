package cloud.qasino.card.components.retry;

import cloud.qasino.card.components.scheduling.Scheduler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * The retry manager takes care of the initialisation of the retry feature.
 * <p>
 * In order to use the retry feature in your project, implement the @{@link RetryCriteria}
 *
 * @see RetryCriteria
 */
@Component
@Slf4j
public class QasinoGameRetryManager {

    @Resource
    private QasinoRequestRetryConfig config;

    private Scheduler scheduler;

    @Resource
    private QasinoGameRetryRecordProvider recordProvider;

    @Resource
    private QasinoGameRetryTask retryTask;

    @PostConstruct
    public void init() {
        if (!config.isRetryOnError()) {
            log.warn("Retry manager is disabled!");
            return;
        }
        scheduler = new Scheduler();
        final Scheduler.ScheduledTask task = scheduler
                .scheduleTask(new RetryTimedOutRequestsTask(
                                recordProvider,
                                retryTask, // This will trigger the event handler
                                new SimpleRetrySchedule(
                                        config.getMaxRetrials(),
                                        config.getRetryIntervalSeconds(),
                                        TimeUnit.SECONDS)
                        ),
                        config.getSchedulerIntervalSeconds(),
                        TimeUnit.SECONDS);
        log.info("######## Initialized with retry task: {} ##########", task);
    }


}
