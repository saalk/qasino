package cloud.qasino.card.entity.enums.playingcard;

import cloud.qasino.card.entity.enums.LabeledEnum;
import cloud.qasino.card.entity.enums.event.Action;
import lombok.Getter;

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
	public static final Map<String, Face> lookup
			= new HashMap<>();

	static {
		for (Face location : EnumSet.allOf(Face.class))
			lookup.put(location.getLabel(), location);
	}

	public static final Map<String, Face> faceMapNoError
			= new HashMap<>();
	static {
		for(Face face : EnumSet.allOf(Face.class))
			if (!face.getLabel().toLowerCase().equals("error"))
				faceMapNoError.put(face.getLabel(), face);
	}

	@Transient
	private String label;

	Face() {
		this.label = "error";

	}

	Face(String label) {
		this();
		this.label = label;
	}

	public static Face fromLabel(String inputLabel) {
		return lookup.get(inputLabel.toLowerCase());
	}

	public static Face fromLabel(char character) {
		return fromLabel(Character.toString(character));
	}

	public static Face fromLabelWithDefault(String label) {
		Face face = fromLabel(label);
		if (face == null) return Face.ERROR;
		return face;
	}

	public static Face fromLabelWithDefault(char character) {
		return fromLabelWithDefault(Character.toString(character));
	}

}
