package cloud.qasino.card.entity.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Column;
import javax.persistence.Transient;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Getter
public enum Face implements LabeledEnum {

	@Column(name = "location", length = 25, nullable = false)
	UP("up"), DOWN("down"), ERROR("error");

	/**
	 * A list of all the Enums in the class. The list is created via Set implementation EnumSet.
	 * <p>EnumSet is an abstract class so new() operator does not work. EnumSet has several static
	 * factory methods for creating an instance like creating groups from enums.
	 * Here it is used to group all enums.
	 */
	public static Set<Face> locations = EnumSet.of(UP, DOWN,
			ERROR);

	/**
	 * A static HashMap lookup with key + value is created to use in a getter
	 * to fromLabel the Enum based on the name eg. key "Low" -> value Location.DUMB
	 */
	private static final Map<String, Face> lookup
			= new HashMap<>();

	static {
		for (Face location : EnumSet.allOf(Face.class))
			lookup.put(location.getLabel(), location);
	}

	@Transient
	private String label;

	Face() {
	}

	Face(String label) {
		this();
		this.label = Face.fromLabel(label).getLabel();
	}

	public static Face fromLabel(String inputLabel) {
		String label = StringUtils.lowerCase(inputLabel);
		return (lookup.get(label) != null) ? lookup.get(label) : ERROR;
	}

}
