package cloud.qasino.games.util;

import org.springframework.stereotype.Component;

@Component
public class Message {

    public String getMessage(String message) {
        return "\n " + message;
    }
}
