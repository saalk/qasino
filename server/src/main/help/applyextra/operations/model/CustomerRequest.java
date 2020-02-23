package applyextra.operations.model;

/**
 * Created by rw38vx on 27-6-17.
 */
public class CustomerRequest{

    public enum Status {
        FULFILLED, IN_PROGRESS, FAILED;
    }

    /**
     * GB - Gespreid Betalen - Change Repayment
     * LW - Limiet Wijziging - Change Limit
     */
    public enum Type {
        GB, // Change repayment
        LW; // Change Limit
    }

    public enum Template{
        SCRDGB01,  // Change repayment
        SCRDGB02,  // Change repayment
        SCRDSP001, //
        SCRDSR001, //
        SCRDCC04;  // Close playingcard
    }
}
