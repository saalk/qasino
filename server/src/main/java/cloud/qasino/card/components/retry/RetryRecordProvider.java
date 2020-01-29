package cloud.qasino.card.components.retry;

import java.util.List;

public interface RetryRecordProvider<RETRY_INPUT> {

    List<RetryRecord<RETRY_INPUT>> getRetryRecords();

    void removeRetryRecord(RETRY_INPUT input);


    void updateRetryRecord(RETRY_INPUT input);
}
