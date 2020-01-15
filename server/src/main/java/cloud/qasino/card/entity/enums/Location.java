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
public enum Location implements LabeledEnum {

	@Column(name = "location", length = 25, nullable = false)
	HAND("hand"), PILE("pile"), TABLE("table"), DISCARD("discard") , ERROR("error");
	
	/**
	 * A list of all the Enums in the class. The list is created via Set implementation EnumSet.
	 * <p>EnumSet is an abstract class so new() operator does not work. EnumSet has several static
	 * factory methods for creating an instance like creating groups from enums.
	 * Here it is used to group all enums.
	 */
	public static Set<Location> locations = EnumSet.of(HAND, PILE,
			TABLE, DISCARD);
	
	/**
	 * A static HashMap lookup with key + value is created to use in a getter
	 * to fromLabel the Enum based on the name eg. key "Low" -> value Location.DUMB
	 */
	private static final Map<String, Location> lookup
			= new HashMap<>();
	
	static {
		for (Location location : EnumSet.allOf(Location.class))
			lookup.put(location.getLabel(), location);
	}
	
	@Transient
	private String label;
	
	Location() {
	}

	Location(String label) {
		this();
		this.label = Location.fromLabel(label).getLabel();
	}

	public static Location fromLabel(String inputLabel) {
		String label = StringUtils.lowerCase(inputLabel);
		return (lookup.get(label) != null) ? lookup.get(label) : ERROR;
	}

}
