package cloud.qasino.card.entity.enums;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Transient;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Getter
public enum Action implements LabeledEnum {
	
	@Column(name = "cardAction", length = 25, nullable = false)
	DEAL("Deal"), HIGHER("Higher"), LOWER("Lower"), PASS("Pass"), NEXT("Next");
	
	/**
	 * A list of all the Enums in the class. The list is created via Set implementation EnumSet.
	 * <p>EnumSet is an abstract class so new() operator does not work. EnumSet has several static
	 * factory methods for creating an instance like creating groups from enums.
	 * Here it is used to group all enums.
	 */
	public static Set<Action> actions = EnumSet.of(DEAL, HIGHER, LOWER, PASS, NEXT);
	
	/**
	 * A static HashMap lookup with key + value is created to use in a getter
	 * to fromLabel the Enum based on the name eg. key "Low" -> value Action.DUMB
	 */
	private static final Map<String, Action> lookup
			= new HashMap<>();
	
	static {
		for (Action action : EnumSet.allOf(Action.class))
			lookup.put(action.getLabel(), action);
	}
	
	@Transient
	private String label;
	
	Action() {
		
	}
	
	Action(String label) {
		this();
		this.label = label;
	}
	
	public static Action fromLabel(String label) {
		return lookup.get(label);
	}
}
