package applyextra.commons.components.retry;

import lombok.extern.slf4j.Slf4j;
import applyextra.commons.components.scheduling.Task;

import java.util.Date;
import java.util.List;

/**
 * Created by CL94WQ on 13-07-16.
 */
@Slf4j
public class RetryTimedOutRequestsTask implements Task {

    private RetryTask retryTask;
    private RetrySchedule retrySchedule;
    private RetryRecordProvider retryRecordProvider;

    public RetryTimedOutRequestsTask(final RetryRecordProvider retryRecordProvider,
                                     final RetryTask retryTask,
                                     final RetrySchedule retrySchedule
    ) {
        this.retryRecordProvider = retryRecordProvider;
        this.retryTask = retryTask;
        this.retrySchedule = retrySchedule;
    }

    @Override
    public String execute() {
        int count = 0;
        // get requests that are stuck on a certain state
        List<RetryRecord> retryRecords = retryRecordProvider.getRetryRecords();
        log.info("retry scheduler processing {} records",retryRecords.size());
        for (RetryRecord retryRecord : retryRecords) {
            if (retrySchedule.mustRetryNow(retryRecord)) {
                log.info("retrying input={}", retryRecord.getRetryInput());
                count++;
                retryRecordProvider.updateRetryRecord(retryRecord.getRetryInput());
                try {
                    if (retryTask.retry(retryRecord.getRetryInput())) {
                        log.info("SUCCESSFULLY retried input={}", retryRecord.getRetryInput());
                        retryRecordProvider.removeRetryRecord(retryRecord.getRetryInput());
                    } else {
                        log.error("ERROR while retrying input={}", retryRecord.getRetryInput());
                    }

                } catch (Exception e) {
                    log.error("EXCEPTION while retrying input={} | exception={}", retryRecord.getRetryInput(), e.getMessage());
                    log.debug("EXCEPTION ", e);
                }
                // update the number of retries in the record
                // this should be moved into the retry record provider
                finally {
                    retryRecord.setLastTryTimestamp(new Date());
                    retryRecord.incrementNrOfRetries();
                }
            } else {
                retryTask.giveUp(retryRecord.getRetryInput());
                retryRecordProvider.removeRetryRecord(retryRecord.getRetryInput());
                log.warn("Not retrying now input={}",retryRecord.getRetryInput());
            }
        }
        return "" + count + " records retried";
    }
}
