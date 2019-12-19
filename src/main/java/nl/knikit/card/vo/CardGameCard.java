package nl.knikit.card.vo;

import lombok.Getter;
import lombok.Setter;
import nl.knikit.card.entity.enums.Rank;
import nl.knikit.card.entity.enums.Suit;
import org.springframework.hateoas.server.core.Relation;

import java.io.Serializable;

@Getter
@Setter
@Relation(value = "card", collectionRelation = "cards")
//@JsonIdentityInfo(generator=JSOGGenerator.class)
public class CardGameCard implements Serializable{
	
	private String cardId;
	private String rank;
	private String suit;
	private int value;
	
	public CardGameCard() {
	}
	
	public Suit getSuitConvertedFromLabel(String suitLabel) throws Exception {
		Suit converted = Suit.fromLabel(suitLabel);
		if (converted==null) {
			throw new Exception("SuitParseLabelException");
		}
		return converted;
	}
	
	public void setSuit(Suit suit) {
		// static Eum methods:
		// - valueOf() - returns enum instance taking a String
		// - values()  - returns all enum instances
		// instance Enum method:
		// - name()    - returns name of enum constant
		// -> better use toString() to get the user-friendly name
		
		this.suit = (String.valueOf(suit));
	}
	
	public Rank getRankConvertedFromLabel(String rankLabel) throws Exception {
		Rank converted = Rank.fromLabel(rankLabel);
		if (converted==null) {
			throw new Exception("RankParseLabelException");
		}
		return converted;
	}
	
	public void setRank(Rank rank) {
		// static Eum methods:
		// - valueOf() - returns enum instance taking a String
		// - values()  - returns all enum instances
		// instance Enum method:
		// - name()    - returns name of enum constant
		// -> better use toString() to get the user-friendly name
		
		this.rank = (String.valueOf(rank));
	}
	
}
