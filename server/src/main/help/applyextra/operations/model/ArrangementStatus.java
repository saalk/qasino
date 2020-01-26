package applyextra.operations.model;

public enum ArrangementStatus {

    ACTIVE(1),
    CLOSED(2);

    private Integer arrangementStatus;

    ArrangementStatus(Integer statusCode) { this.arrangementStatus = arrangementStatus;}

    public Integer getArrangementStatus() { return this.arrangementStatus; }
}
