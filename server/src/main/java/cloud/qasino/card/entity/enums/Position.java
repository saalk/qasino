package cloud.qasino.card.entity.enums;

import cloud.qasino.card.domain.qasino.style.MaxAnte;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Column;
import javax.persistence.Transient;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Getter
public enum Position implements LabeledEnum {

	@Column(name = "location", length = 25, nullable = false)
	SHUFFLED("shuffled"), ORDERED("ordered"), MANUAL("manual"), ERROR("error");

	/**
	 * A list of all the Enums in the class. The list is created via Set implementation EnumSet.
	 * <p>EnumSet is an abstract class so new() operator does not work. EnumSet has several static
	 * factory methods for creating an instance like creating groups from enums.
	 * Here it is used to group all enums.
	 */
	public static Set<Position> locations = EnumSet.of(SHUFFLED, ORDERED, MANUAL,
			ERROR);

	/**
	 * A static HashMap lookup with key + value is created to use in a getter
	 * to fromLabel the Enum based on the name eg. key "Low" -> value Location.DUMB
	 */
	private static final Map<String, Position> lookup
			= new HashMap<>();

	static {
		for (Position location : EnumSet.allOf(Position.class))
			lookup.put(location.getLabel(), location);
	}

	@Transient
	private String label;

	Position() {
		this.label = "error";
	}

	Position(String label) {
		this();
		this.label = label;
	}

	public static Position fromLabel(String inputLabel) {
		return lookup.get(inputLabel.toLowerCase());
	}

	public static Position fromLabel(char character) {
		return fromLabel(Character.toString(character));
	}

	public static Position fromLabelWithDefault(String label) {
		Position position = fromLabel(label);
		if (position == null) return Position.ERROR;
		return position;
	}

	public static Position fromLabelWithDefault(char character) {
		return fromLabelWithDefault(Character.toString(character));
	}

}
