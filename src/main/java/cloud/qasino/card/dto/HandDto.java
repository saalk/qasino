package cloud.qasino.card.dto;

import cloud.qasino.card.entity.Card;
import cloud.qasino.card.entity.Hand;
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
@Relation(value = "hand", collectionRelation = "hands")
//@JsonIdentityInfo(generator=JSOGGenerator.class)
// - this annotation adds @Id to prevent chain loop
// - you could also use @JsonManagedReference and @JsonBackReference
public class HandDto implements Serializable {
	
	public HandDto() {
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
	private PlayerDto playerDto;
	//@JsonManagedReference(value="playerDto")
	@JsonIgnore
	private CasinoDto casinoDto;
	@JsonProperty(value = "card")
	private CardDto cardDto;
	private int cardOrder;
	@JsonIgnore
	private String cardLocation;
	@JsonIgnore
	private String cardAction;
	@JsonIgnore
	private int round;
	@JsonIgnore
	private int turn;
	
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
		if (cardDto ==null) {
			new Exception("CardId cannot be null in HandDto") ;
		}
		if (this.cardOrder < 10) {
			sb.append("(0" + this.cardOrder + ") ");
		} else {
			sb.append("(" + this.cardOrder + ") ");
		}
		if (this.cardDto.getCardId().length() == 2) {
			sb.append(" ");
		}
		sb.append(this.cardDto.getCardId());
		if (this.playerDto == null || this.playerDto.getPlayerId() == 0) {
			sb.append("  " +
					          WordUtils.capitalizeFully(this.cardDto.getRank()) + " of " +
					          WordUtils.capitalizeFully(this.cardDto.getSuit()));
		} else {
			sb.append(
					"  " +
							WordUtils.capitalizeFully(this.playerDto.getAlias()) + " [" +
							WordUtils.capitalizeFully(this.playerDto.getAiLevel()) + "]");
		}
		this.name = String.valueOf(sb);
	}
	
}
	

