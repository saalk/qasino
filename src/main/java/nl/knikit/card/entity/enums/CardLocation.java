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
public enum CardLocation implements LabeledEnum {
	
	@Column(name = "CARD_LOCATION", length = 25, nullable = false)
	STACK("Stock"), HAND("Hand"), DRAW_PILE("Draw pile"), TABLE("Center stock"), DISCARD_PILE("Discard pile");
	
	/**
	 * A list of all the Enums in the class. The list is created via Set implementation EnumSet.
	 * <p>EnumSet is an abstract class so new() operator does not work. EnumSet has several static
	 * factory methods for creating an instance like creating groups from enums.
	 * Here it is used to group all enums.
	 */
	public static Set<CardLocation> CardLocations = EnumSet.of(STACK, HAND, DRAW_PILE, TABLE, DISCARD_PILE);
	
	/**
	 * A static HashMap lookup with key + value is created to use in a getter
	 * to fromLabel the Enum based on the name eg. key "Low" -> value CardLocation.DUMB
	 */
	private static final Map<String, CardLocation> lookup
			= new HashMap<>();
	
	static {
		for (CardLocation cardLocation : EnumSet.allOf(CardLocation.class))
			lookup.put(cardLocation.getLabel(), cardLocation);
	}
	
	@Transient
	private String label;
	
	CardLocation() {
		
	}
	
	CardLocation(String label) {
		this();
		this.label = label;
	}
	
	public static CardLocation fromLabel(String label) {
		return lookup.get(label);
	}
}
