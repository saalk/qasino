package cloud.qasino.games.error;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {

    private String code;

    private String message;

    private String solution;

    public ErrorResponse() {
    }

    public ErrorResponse(HttpError httpError, String expected, String supplied, String domain) {
        this.code = String.valueOf(httpError.getLabel());
        this.message = httpError.getMessage();
        String options = ", All: [";
        if (!domain.isEmpty()) {
            switch (domain) {
                case "state":
                    options += "init, setup, shuffle, move]";
                    break;
                case "init":
                    options += "gametType, ante]";
                    break;
                case "setup":
                    options += "visitorName, ante]";
                    break;
                case "shuffle":
                    options += "jokers, suit]";
                    break;
                default:
                    options = "";
            }
        }
        this.solution = "Expected: [" + expected + "], Supplied: [" + supplied + "]" + options;

    }
}
