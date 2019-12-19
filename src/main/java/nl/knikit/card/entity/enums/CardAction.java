package nl.knikit.card.entity.enums;

import lombok.Getter;
import nl.knikit.card.entity.enums.enumlabel.LabeledEnum;

import javax.persistence.Column;
import javax.persistence.Transient;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Getter
public enum CardAction implements LabeledEnum {
	
	@Column(name = "CARD_LOCATION", length = 25, nullable = false)
	DEAL("Deal"), HIGHER("Higher"), LOWER("Lower"), PASS("Pass"), NEXT("Next");
	
	/**
	 * A list of all the Enums in the class. The list is created via Set implementation EnumSet.
	 * <p>EnumSet is an abstract class so new() operator does not work. EnumSet has several static
	 * factory methods for creating an instance like creating groups from enums.
	 * Here it is used to group all enums.
	 */
	public static Set<CardAction> CardActions = EnumSet.of(DEAL, HIGHER, LOWER, PASS, NEXT);
	
	/**
	 * A static HashMap lookup with key + value is created to use in a getter
	 * to fromLabel the Enum based on the name eg. key "Low" -> value CardAction.DUMB
	 */
	private static final Map<String, CardAction> lookup
			= new HashMap<>();
	
	static {
		for (CardAction cardAction : EnumSet.allOf(CardAction.class))
			lookup.put(cardAction.getLabel(), cardAction);
	}
	
	@Transient
	private String label;
	
	CardAction() {
		
	}
	
	CardAction(String label) {
		this();
		this.label = label;
	}
	
	public static CardAction fromLabel(String label) {
		return lookup.get(label);
	}
}
