package nl.knikit.card.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import nl.knikit.card.entity.enums.GameType;
import nl.knikit.card.entity.GameVariant;
import nl.knikit.card.statemachine.CardGameStateMachine;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Getter
@Setter
//@Relation(value = "cardgame", collectionRelation = "cardgames")
//@JsonIdentityInfo(generator = JSOGGenerator.class)
// - this annotation adds @Id to prevent chain loop
// - you could also use @JsonManagedReference and @JsonBackReference
public class CardGame implements Serializable {
	
	public CardGame() {
	}
	
	// CardGame has 14 fields, CardGameDto has 3 more
	
	// discard lombok setter for this field -> make your own
	@Setter(AccessLevel.NONE)
	private String name; // extra field "Highlow:0005 (Ante:100) [GameSelected]"
	private int gameId;
	// private String created; to prevent setting, this is generated
	private CardGameStateMachine.State state;
	private GameType gameType;
	private String gameVariant;
	private int ante;
	@Setter(AccessLevel.NONE)
	private String dealt; // extra field
	@Setter(AccessLevel.NONE)
	private String stack; // extra field
	private int currentRound;
	@JsonProperty(value = "currentPlayerId")
	private int activeCasino;
	
	@JsonIgnore
	//@JsonBackReference(value="gameDto")
	@JsonProperty(value = "cardsInDeck")
	@Setter(AccessLevel.NONE)
	@Getter(AccessLevel.NONE)
	private List<CardGameDeck> cards;
	
	@JsonProperty(value = "players")
	@Setter(AccessLevel.NONE)
	@Getter(AccessLevel.NONE)
	private List<CardGameCasino> players;
	
	// "10C  Ten of Clubs"
	// "  *  40 cards left
	// "---  -------------
	// " AS+ Script Joe [ELF]-"
	// " RJ  Script Joe [ELF]"
	//@JsonManagedReference(value="playerDto")
	@JsonProperty(value = "winner")
	@Setter(AccessLevel.NONE)
	private CardGamePlayer winner;
	
	@JsonIgnore
	public GameType getGameTypeFromLabel(String gameType) throws Exception {
		GameType converted = GameType.fromLabel(gameType);
		if (converted == null) {
			throw new Exception("GameTypeParseLabelException");
		}
		return converted;
	}
	
	@JsonIgnore
	public GameVariant getGameVariantFromLabel(String gameVariant) throws Exception {
		GameVariant converted = new GameVariant(gameVariant);
		if (converted == null) {
			throw new Exception("GameVariantParseLabelException");
		}
		return converted;
	}
	
/*	@JsonIgnore
	public State getStateConverted(String state) throws Exception {
		State converted = State.valueOf(state);
		if (converted == null) {
			throw new Exception("StateParseException");
		}
		return converted;
	}*/
	
	public void setName() {
		
		// "Highlow#0005 (Ante:100) [IS_SHUFFLED]"
		this.name = WordUtils.capitalizeFully(this.gameType.toString()) + "#" +
				            StringUtils.leftPad(String.valueOf(this.gameId), 4, "0");
	}
	
	public void setWinner(CardGamePlayer cardGamePlayer) {
		this.winner = cardGamePlayer;
	}
	
	public CardGamePlayer getWinner() {
		return this.winner;
	}
	
	public void setCardsDealt() {
		if (this.cards != null) {
			StringBuilder sb = new StringBuilder(" card(s) dealt [");
			List<CardGameDeck> decks;
			decks = this.cards;
			// sort on card order
			Collections.sort(decks, Comparator.comparing(CardGameDeck::getCardOrder).thenComparing(CardGameDeck::getCardOrder));
			boolean first = true;
			int count = 0;
			
			for (CardGameDeck deck : decks) {
				
				if (!deck.getCardLocation().equals("STACK")) {
					count++;
					if (!first) {
						sb.append(" ");
					}
					first = false;
					sb.append(deck.getCardGameCard().getCardId());
				}
			}
			sb.append("]");
			sb.insert(0, count);
			
			this.dealt = sb.toString();
		} else {
			this.dealt = "0 cards []";
		}
	}
	
	public void setCardsLeft() {
		if (this.cards != null) {
			StringBuilder sb = new StringBuilder(" card(s) [");
			List<CardGameDeck> decks;
			decks = this.cards;
			// sort on card order
			Collections.sort(decks, Comparator.comparing(CardGameDeck::getCardOrder).thenComparing(CardGameDeck::getCardOrder));
			boolean first = true;
			int count = 0;
			for (CardGameDeck deck : decks) {
				if (deck.getCardLocation().equals("STACK")) {
					count++;
					if (!first) {
						sb.append(" ");
					}
					first = false;
					sb.append(deck.getCardGameCard().getCardId());
				}
			}
			sb.append("]");
			sb.insert(0, count);
			this.stack = sb.toString();
		} else {
			this.stack = "0 cards []";
		}
	}
	
	@JsonIgnore
	public void setCards(List<CardGameDeck> cardGameDecks) {
		this.cards = cardGameDecks;
		this.setCardsDealt();
		this.setCardsLeft();
	}
	
	@JsonIgnore
	public List<CardGameDeck> getCards() {
		return this.cards;
	}
	
	@JsonIgnore
	public void setPlayers(List<CardGameCasino> cardGameCasinos) {
		List<CardGameCasino> newCardGameCasinos = new ArrayList<>();
		for (CardGameCasino cardGameCasino :
				cardGameCasinos) {
			cardGameCasino.setBalance();
			newCardGameCasinos.add(cardGameCasino);
		}
		this.players = newCardGameCasinos;
	}
	
	@JsonIgnore
	public List<CardGameCasino> getPlayers() {
		
		return this.players;
	}
	
}
