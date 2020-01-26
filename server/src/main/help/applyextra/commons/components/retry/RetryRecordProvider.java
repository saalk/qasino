package applyextra.commons.components.retry;

import java.util.List;

/**
 * Created by CL94WQ on 13-07-16.
 */
public interface RetryRecordProvider<RETRY_INPUT> {

    List<RetryRecord<RETRY_INPUT>> getRetryRecords();

    void removeRetryRecord(RETRY_INPUT input);


    void updateRetryRecord(RETRY_INPUT input);
}
