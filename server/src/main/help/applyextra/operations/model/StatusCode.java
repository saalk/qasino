package applyextra.operations.model;

public enum StatusCode {

    ACTIVE("1"),
    CLOSED("0");

    private String statusCode;

    StatusCode(String statusCode) { this.statusCode = statusCode;}

    public String getStatusCode() { return this.statusCode; }

}
