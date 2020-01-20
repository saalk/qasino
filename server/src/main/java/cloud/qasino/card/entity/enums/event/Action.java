package cloud.qasino.card.entity.enums.event;

import cloud.qasino.card.domain.qasino.style.MaxAnte;
import cloud.qasino.card.entity.enums.LabeledEnum;
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
	DEAL("deal"), HIGHER("higher"), LOWER("lower"), PASS("pass"), NEXT("next"), ERROR("error");
	
	/**
	 * A list of all the Enums in the class. The list is created via Set implementation EnumSet.
	 * <p>EnumSet is an abstract class so new() operator does not work. EnumSet has several static
	 * factory methods for creating an instance like creating groups from enums.
	 * Here it is used to group all enums.
	 */
	public static Set<Action> actions = EnumSet.of(DEAL, HIGHER, LOWER, PASS, NEXT, ERROR);
	
	/**
	 * A static HashMap lookup with key + value is created to use in a getter
	 * to fromLabel the Enum based on the name eg. key "Low" -> value Action.DUMB
	 */
	public static final Map<String, Action> lookup
			= new HashMap<>();
	
	static {
		for (Action action : EnumSet.allOf(Action.class))
			lookup.put(action.getLabel(), action);
	}

	public static final Map<String, Action> actionMapNoError
			= new HashMap<>();
	static {
		for(Action action : EnumSet.allOf(Action.class))
			if (!action.getLabel().toLowerCase().equals("error"))
				actionMapNoError.put(action.getLabel(), action);
	}


	@Transient
	private String label;
	
	Action() {
		this.label = "error";
	}

	Action(String label) {
		this();
		this.label = label;
	}

	public static Action fromLabel(String inputLabel) {
		return lookup.get(inputLabel.toLowerCase());
	}

	public static Action fromLabel(char character) {
		return fromLabel(Character.toString(character));
	}

	public static Action fromLabelWithDefault(String label) {
		Action action = fromLabel(label);
		if (action == null) return Action.ERROR;
		return action;
	}

	public static Action fromLabelWithDefault(char character) {
		return fromLabelWithDefault(Character.toString(character));
	}

}
