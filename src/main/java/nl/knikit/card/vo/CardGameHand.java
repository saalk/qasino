package nl.knikit.card.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import nl.knikit.card.entity.Card;
import nl.knikit.card.entity.enums.CardAction;
import nl.knikit.card.entity.enums.CardLocation;
import nl.knikit.card.entity.Hand;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import org.springframework.hateoas.server.core.Relation;

import java.io.Serializable;

@Getter
@Setter
@Relation(value = "hand", collectionRelation = "hands")
//@JsonIdentityInfo(generator=JSOGGenerator.class)
// - this annotation adds @Id to prevent chain loop
// - you could also use @JsonManagedReference and @JsonBackReference
public class CardGameHand implements Serializable {
	
	public CardGameHand() {
	}
	
	// Hand has 5 fields, HandDto has 1 more
	// discard lombok setter for this field -> make your own
	@Setter(AccessLevel.NONE)
	private String name; // extra field
	// "(03) 10C  Ten of Clubs"
	// "       *  40 cards left
	// "---- ---  -------------
	// "(01)  AS+ Script Joe [ELF]-"
	// "(02)  RJ  Script Joe [ELF]"
	private int handId;
	@JsonIgnore
	private CardGamePlayer cardGamePlayer;
	//@JsonManagedReference(value="playerDto")
	@JsonIgnore
	private CardGameCasino cardGameCasino;
	@JsonProperty(value = "card")
	private CardGameCard cardGameCard;
	private int cardOrder;
	@JsonIgnore
	private String cardLocation;
	@JsonIgnore
	private String cardAction;
	@JsonIgnore
	private int round;
	@JsonIgnore
	private int turn;
	
	public void setCardLocation(CardLocation cardLocation) {
		// static Eum methods:
		// - valueOf() - returns enum instance taking a String
		// - values()  - returns all enum instances
		// instance Enum method:
		// - name()    - returns name of enum constant
		// -> better use toString() to get the user-friendly name
		this.cardLocation = (String.valueOf(cardLocation));
	}
	
	public void setCardAction(CardAction cardAction) {
		// static Eum methods:
		// - valueOf() - returns enum instance taking a String
		// - values()  - returns all enum instances
		// instance Enum method:
		// - name()    - returns name of enum constant
		// -> better use toString() to get the user-friendly name
		this.cardAction = (String.valueOf(cardAction));
	}
	
	public Hand getNameConverted(String name) {
		// "10C  Ten of Clubs"
		// " AS+ Script Joe [ELF]-"
		String[] splitName = StringUtils.split(StringUtils.replace(StringUtils.replace(name, " of ", ";"), " ", ";"), ";");
		
		if (splitName.length != 3 ||
				    splitName[0].isEmpty() || splitName[1].isEmpty() || splitName[2].isEmpty()) {
			Hand newHand = new Hand();
			newHand.setHandId(0);
			newHand.setCard(new Card("AS"));
			return newHand;
		}
		Hand newHand = new Hand();
		newHand.setCard(new Card(splitName[0]));
		return newHand;
	}
	
	public void setName() throws Exception {
		// "10C  Ten of Clubs"
		
		StringBuilder sb = new StringBuilder();
		if (cardGameCard ==null) {
			new Exception("CardId cannot be null in HandDto") ;
		}
		if (this.cardOrder < 10) {
			sb.append("(0" + this.cardOrder + ") ");
		} else {
			sb.append("(" + this.cardOrder + ") ");
		}
		if (this.cardGameCard.getCardId().length() == 2) {
			sb.append(" ");
		}
		sb.append(this.cardGameCard.getCardId());
		if (this.cardGamePlayer == null || this.cardGamePlayer.getPlayerId() == 0) {
			sb.append("  " +
					          WordUtils.capitalizeFully(this.cardGameCard.getRank()) + " of " +
					          WordUtils.capitalizeFully(this.cardGameCard.getSuit()));
		} else {
			sb.append(
					"  " +
							WordUtils.capitalizeFully(this.cardGamePlayer.getAlias()) + " [" +
							WordUtils.capitalizeFully(this.cardGamePlayer.getAiLevel()) + "]");
		}
		this.name = String.valueOf(sb);
	}
	
}
	

