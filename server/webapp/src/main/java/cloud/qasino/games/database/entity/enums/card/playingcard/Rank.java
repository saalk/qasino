package cloud.qasino.games.database.entity.enums.card.playingcard;

import cloud.qasino.games.database.entity.enums.game.Type;
import cloud.qasino.games.database.entity.enums.LabeledEnum;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Column;
import javax.persistence.Transient;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
public enum Rank implements LabeledEnum {

    @Column(name = "rank", length = 10, nullable = false)
    ACE("A"), TWO("2"), THREE("3"), FOUR("4"), FIVE("5"), SIX("6"), SEVEN("7"), EIGHT("8"),
    NINE("9"), TEN("10"), JACK("J"), QUEEN("Q"), KING("K"), JOKER("R");

    public static final Map<String, Rank> rankMapNoError
            = new HashMap<>();
    private static final Map<String, Rank> lookup
            = new HashMap<>();

    static {
        for (Rank rank : EnumSet.allOf(Rank.class))
            lookup.put(rank.getLabel(), rank);
    }

    static {
        for (Rank rank : EnumSet.allOf(Rank.class))
            if (!rank.getLabel().toLowerCase().equals("error"))
                rankMapNoError.put(rank.getLabel(), rank);
    }

    @Transient
    private String label;

    Rank() {
    }

    Rank(String label) {
        this();
        this.label = label;
    }

    public static Rank fromLabel(String inputLabel) {
        String label = StringUtils.upperCase(inputLabel);
        try {
            Rank.lookup.get(label);
        } catch (Exception e) {
            return null;
        }
        return Rank.lookup.get(label);
    }

    public int overrideValue(Type inputType) {
        switch (inputType) {
            default:
            case HIGHLOW:
                //@formatter:off
                switch (Objects.requireNonNull(fromLabel(this.label))) {
                    case JOKER: return 0;
                    case ACE: return 1;
                    case KING: return 13;
                    case QUEEN: return 12;
                    case JACK: return 11;
                    default: return Integer.parseInt(label);
                }
                //@formatter: off
        }
    }
}

