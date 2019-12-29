package cloud.qasino.card.entity.enums;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Transient;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Getter
public enum Move implements LabeledEnum {
	
	@Column(name = "move", length = 25, nullable = false)
	HAND_OPEN("Hand open"), HAND_CLOSED("Hand closed"), PILE_DRAW("Draw pile"), TABLE_MIDDLE(
			"Center stock"),	PILE_DISCARD(
			"Discard pile");
	
	/**
	 * A list of all the Enums in the class. The list is created via Set implementation EnumSet.
	 * <p>EnumSet is an abstract class so new() operator does not work. EnumSet has several static
	 * factory methods for creating an instance like creating groups from enums.
	 * Here it is used to group all enums.
	 */
	public static Set<Move> moves = EnumSet.of(HAND_OPEN, HAND_CLOSED, PILE_DRAW,
			TABLE_MIDDLE, PILE_DISCARD);
	
	/**
	 * A static HashMap lookup with key + value is created to use in a getter
	 * to fromLabel the Enum based on the name eg. key "Low" -> value Move.DUMB
	 */
	private static final Map<String, Move> lookup
			= new HashMap<>();
	
	static {
		for (Move move : EnumSet.allOf(Move.class))
			lookup.put(move.getLabel(), move);
	}
	
	@Transient
	private String label;
	
	Move() {
		
	}
	
	Move(String label) {
		this();
		this.label = label;
	}
	
	public static Move fromLabel(String label) {
		return lookup.get(label);
	}
}
