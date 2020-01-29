package cloud.qasino.card.components.retry;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class RetryRecord<RETRY_INPUT> {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:SSS")
    private Date lastTryTimestamp;
    private int nrOfRetriesAttempted;
    private RETRY_INPUT retryInput;


    public RetryRecord() {
    }

    public RetryRecord(final RETRY_INPUT retryInput, final Date lastRetryTimestamp, final int nrOfRetriesAttempted) {
        this.retryInput = retryInput;
        this.lastTryTimestamp = lastRetryTimestamp;
        this.nrOfRetriesAttempted = nrOfRetriesAttempted;
    }

    public void incrementNrOfRetries() {
        nrOfRetriesAttempted++;
    }

    public Date getLastTryTimestamp() {
        return lastTryTimestamp;
    }

    public int getNrOfRetriesAttempted() {
        return nrOfRetriesAttempted;
    }

    public RETRY_INPUT getRetryInput() {
        return retryInput;
    }


}
