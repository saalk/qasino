package cloud.qasino.card.vo;

import cloud.qasino.card.entity.Card;
import cloud.qasino.card.entity.Deck;
import cloud.qasino.card.entity.enums.CardLocation;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import org.springframework.hateoas.server.core.Relation;

import java.io.Serializable;

@Getter
@Setter
@Relation(value = "deck", collectionRelation = "decks")
//@JsonIdentityInfo(generator=JSOGGenerator.class)
// - this annotation adds @Id to prevent chain loop
// - you could also use @JsonManagedReference and @JsonBackReference
public class CardGameDeck implements Serializable {
	
	public CardGameDeck() {
	}
	
	// Deck has 5 fields, DeckDto has 1 more
	// discard lombok setter for this field -> make your own
	@Setter(AccessLevel.NONE)
	private String name; // extra field
	// "(03) 10C  Ten of Clubs"
	// "       *  40 cards left
	// "---- ---  -------------
	// "(01)  AS+ Script Joe [ELF]-"
	// "(02)  RJ  Script Joe [ELF]"
	@JsonIgnore
	private int deckId;
	//@JsonManagedReference(value="gameDto")
	@JsonIgnore
	private CardGame cardGame;
	@JsonProperty(value = "card")
	private CardGameCard cardGameCard;
	private int cardOrder;
	private String cardLocation;
	@JsonProperty(value = "player")
	private CardGameCasino dealtToCardGameCasino;
	
	public Deck getNameConverted(String name) {
		// "10C  Ten of Clubs"
		// " AS+ Script Joe [ELF]-"
		String[] splitName = StringUtils.split(StringUtils.replace(StringUtils.replace(name, " of ", ";"), " ", ";"), ";");
		
		if (splitName.length != 3 ||
				    splitName[0].isEmpty() || splitName[1].isEmpty() || splitName[2].isEmpty()) {
			Deck newDeck = new Deck();
			newDeck.setDeckId(0);
			newDeck.setCard(new Card("AS"));
			return newDeck;
		}
		Deck newDeck = new Deck();
		newDeck.setCard(new Card(splitName[0]));
		return newDeck;
	}
	
	public void setName() throws Exception {
		// "10C  Ten of Clubs"
		// " AS+ Script Joe [Human]-"
		
		StringBuilder sb = new StringBuilder();
		if (cardGameCard ==null) {
			new Exception("CardId cannot be null in DeckDto") ;
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
//		if (this.dealtToDto == null || this.dealtToDto.getCasinoId() == 0) {
			sb.append("  " +
					          WordUtils.capitalizeFully(this.cardGameCard.getRank()) + " of " +
					          WordUtils.capitalizeFully(this.cardGameCard.getSuit()));
			//TODO get the player via the casino
//		} else {
//			sb.append(
//					"  " +
//							WordUtils.capitalizeFully(this.dealtToDto.getPlayerDto().getAlias()) + " [" +
//							WordUtils.capitalizeFully(this.dealtToDto.getPlayerDto().getAiLevel()) + "]");
//		}
		this.name = String.valueOf(sb);
	}
	
	public void setCardLocation(CardLocation cardLocation) {
		// static Eum methods:
		// - valueOf() - returns enum instance taking a String
		// - values()  - returns all enum instances
		// instance Enum method:
		// - name()    - returns name of enum constant
		// -> better use toString() to get the user-friendly name
		this.cardLocation = (String.valueOf(cardLocation));
	}
}
	

