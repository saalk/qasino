package cloud.qasino.games.pattern.statemachine.event;

import cloud.qasino.games.pattern.statemachine.event.interfaces.Event;
import jakarta.persistence.Transient;
import lombok.Getter;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

@Getter
public enum QasinoEvent implements Event {

    // vistor events
    LOGON("logon"),
    REPAY("repay"),
    PAWN("pawn"),

    REGISTER("register"),
    UPDATE_VISITOR("update_visitor"),
    DELETE_VISITOR("delete_visitor"),

    // league events
    CREATE_LEAGUE("create_visitor"),
    UPDATE_LEAGUE("update_visitor"),
    DELETE_LEAGUE("delete_visitor"),

    // misc
    NONE("none"),
    ERROR("error");

    public static final Map<String, QasinoEvent> lookup
            = new HashMap<>();

    static {
        for (QasinoEvent playEvent : EnumSet.allOf(QasinoEvent.class))
            lookup.put(playEvent.getLabel(), playEvent);
    }

    public static QasinoEvent fromLabel(String inputLabel) {
        return lookup.get(inputLabel.toLowerCase());
    }

    @Transient
    private String label;

    QasinoEvent() {
        this.label = "error";
    }

    QasinoEvent(String label) {
        this();
        this.label = label;
    }
    public static QasinoEvent fromLabel(char character) {
        return fromLabel(Character.toString(character));
    }

    public static QasinoEvent fromLabelWithDefault(String label) {
        QasinoEvent qasinoEvent = fromLabel(label);
        if (qasinoEvent == null) return QasinoEvent.ERROR;
        return qasinoEvent;
    }

    public static QasinoEvent fromLabelWithDefault(char character) {
        return fromLabelWithDefault(Character.toString(character));
    }
}
