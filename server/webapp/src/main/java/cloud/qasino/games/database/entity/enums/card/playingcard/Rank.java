package cloud.qasino.games.database.entity.enums.card.playingcard;

import cloud.qasino.games.database.entity.enums.game.Type;
import cloud.qasino.games.database.entity.enums.LabeledEnum;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import jakarta.persistence.Column;
import jakarta.persistence.Transient;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
public enum Rank implements LabeledEnum {

    @Column(name = "rank", length = 10, nullable = false)
    ACE("A"), TWO("2"), THREE("3"), FOUR("4"), FIVE("5"), SIX("6"), SEVEN("7"), EIGHT("8"),
    NINE("9"), TEN("10"), JACK("J"), QUEEN("Q"), KING("K"), JOKER("J");

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
            if (!rank.getLabel().equalsIgnoreCase("error"))
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
                return switch (Objects.requireNonNull(fromLabel(this.label))) {case JOKER->0;case ACE->1;case KING->13;case QUEEN->12;case JACK->11;default->Integer.parseInt(label);};
                //@formatter: off
        }
    }
}

