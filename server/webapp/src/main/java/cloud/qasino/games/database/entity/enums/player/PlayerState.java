package cloud.qasino.games.database.entity.enums.player;

import cloud.qasino.games.database.entity.enums.LabeledEnum;
import lombok.Getter;

import jakarta.persistence.Column;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Getter
public enum PlayerState implements LabeledEnum {

    @Column(name = "role", length = 25)
    INITIATOR("initiator"),
    INVITED("invited"),
    ACCEPTED("accepted"),
    REJECTED("rejected"),
    BOT("bot"),
    ERROR("error");

    /**
     * A static HashMap lookup with key + value is created to use in a getter
     * to fromLabel the Enum based on the name eg. key "Low" -> value AiLevel.DUMB
     */
    public static final Map<String, PlayerState> lookup
            = new HashMap<>();
    static {
        for(PlayerState playerState : EnumSet.allOf(PlayerState.class))
            lookup.put(playerState.getLabel(), playerState);
    }
    public static final Map<String, PlayerState> roleMapNoError
            = new HashMap<>();
    static {
        for(PlayerState playerState : EnumSet.allOf(PlayerState.class))
            if (!playerState.getLabel().toLowerCase().equals("error"))
                roleMapNoError.put(playerState.getLabel(), playerState);
    }

    private String label;

    PlayerState() {
        this.label = "error";
    }

    PlayerState(String label) {
        this();
        this.label = label;
    }

    public static PlayerState fromLabel(String inputLabel) {
        return lookup.get(inputLabel.toLowerCase());
    }

    public static PlayerState fromLabel(char character) {
        return fromLabel(Character.toString(character));
    }

    public static PlayerState fromLabelWithDefault(String label) {
        PlayerState playerState = fromLabel(label);
        if (playerState == null) return PlayerState.ERROR;
        return playerState;
    }

    public static PlayerState fromLabelWithDefault(char character) {
        return fromLabelWithDefault(Character.toString(character));
    }

    public static Set<PlayerState> playerStateListActivePlayer = EnumSet.of(INITIATOR, BOT, ACCEPTED);

}
