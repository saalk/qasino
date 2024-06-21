package cloud.qasino.games.database.entity.enums.player;

import cloud.qasino.games.database.entity.enums.LabeledEnum;
import lombok.Getter;

import jakarta.persistence.Column;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Getter
public enum PlayerType implements LabeledEnum {

    @Column(name = "role", length = 25)
    INITIATOR("initiator"),
    INVITED("invited"),
    INVITEE("invitee"),
    REJECTED("rejected"),
    BOT("bot"),
    ERROR("error");

    /**
     * A static HashMap lookup with key + value is created to use in a getter
     * to fromLabel the Enum based on the name eg. key "Low" -> value AiLevel.DUMB
     */
    public static final Map<String, PlayerType> lookup
            = new HashMap<>();
    static {
        for(PlayerType playerType : EnumSet.allOf(PlayerType.class))
            lookup.put(playerType.getLabel(), playerType);
    }
    public static final Map<String, PlayerType> roleMapNoError
            = new HashMap<>();
    static {
        for(PlayerType playerType : EnumSet.allOf(PlayerType.class))
            if (!playerType.getLabel().toLowerCase().equals("error"))
                roleMapNoError.put(playerType.getLabel(), playerType);
    }

    private String label;

    PlayerType() {
        this.label = "error";
    }

    PlayerType(String label) {
        this();
        this.label = label;
    }

    public static PlayerType fromLabel(String inputLabel) {
        return lookup.get(inputLabel.toLowerCase());
    }

    public static PlayerType fromLabel(char character) {
        return fromLabel(Character.toString(character));
    }

    public static PlayerType fromLabelWithDefault(String label) {
        PlayerType playerType = fromLabel(label);
        if (playerType == null) return PlayerType.ERROR;
        return playerType;
    }

    public static PlayerType fromLabelWithDefault(char character) {
        return fromLabelWithDefault(Character.toString(character));
    }

    public static Set<PlayerType> playerStateListActivePlayer = EnumSet.of(INITIATOR, BOT, INVITEE);

}
