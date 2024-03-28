package cloud.qasino.card.response;

import cloud.qasino.card.database.entity.Game;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QasinoResponse {

    private Game cardGame;

    private Reason reason;
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)

    private String errorCode;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private String errorMessage;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private String solution;

    @JsonIgnore
    public Reason getReason() {
        return reason;
    }

    @JsonIgnore
    public void setReason(Reason reason) {
        this.reason = reason;
    }

    @JsonIgnore
    public String getErrorCode() {
        return errorCode;
    }

    @JsonIgnore
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    @JsonIgnore
    public String getSolution() {
        return solution;
    }

    @JsonIgnore
    public void setSolution(String solution) {
        this.solution = solution;
    }

    @JsonIgnore
    public String getErrorMessage() {
        return errorMessage;
    }

    @JsonIgnore
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public enum Reason {
        SUCCESS, FAILURE;
    }
}