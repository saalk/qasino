package applyextra.commons.model;

public class CustomerMessage {

    public enum Status {
        FULFILLED, IN_PROGRESS, FAILED;
    }

    public enum Type {
        GB,LW;
    }

    public enum Template{
        SCRDGB01,SCRDGB02, SCRDCC01, SCRDCC02, SCRDCC03;
    }
}