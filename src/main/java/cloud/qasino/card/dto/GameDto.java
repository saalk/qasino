package cloud.qasino.card.dto;

import cloud.qasino.card.entity.Game;
import cloud.qasino.card.entity.enums.GameType;
import cloud.qasino.card.statemachine.CardGameStateMachine;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import org.springframework.hateoas.server.core.Relation;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

//import nl.knikit.card.model.State;

//import static nl.knikit.cardgames.model.state.CardGameStateMachine.State;

@Getter
@Setter
@Relation(value = "game", collectionRelation = "games")
//@JsonIdentityInfo(generator = JSOGGenerator.class)
// - this annotation adds @Id to prevent chain loop
// - you could also use @JsonManagedReference and @JsonBackReference
public class GameDto implements Serializable {
	
	public GameDto() {
	}
	
	// Game has 14 fields, GameDto has 3 more
	
	// discard lombok setter for this field -> make your own
	@Setter(AccessLevel.NONE)
	private String name; // extra field "Highlow:0005 (Ante:100) [GameSelected]"
	private int gameId;
	// private String created; to prevent setting, this is generated
	private CardGameStateMachine.State state;
	private GameType gameType;
	private int ante;
	@Setter(AccessLevel.NONE)
	private String dealt; // extra field
	@Setter(AccessLevel.NONE)
	private String stock; // extra field
	private int currentRound;
	@JsonProperty(value = "activePlayer")
	private int activeCasino;
	
	//@JsonBackReference(value="gameDto")
	//@JsonProperty(value = "decks")
	@JsonIgnore
	@Setter(AccessLevel.NONE)
	private List<DeckDto> deckDtos;
	
	//@JsonBackReference(value="gameDto")
	//@JsonProperty(value = "decks")
	@JsonIgnore
	@Setter(AccessLevel.NONE)
	private List<CasinoDto> casinoDtos;
	
	// "10C  Ten of Clubs"
	// "  *  40 cards left
	// "---  -------------
	// " AS+ Script Joe [ELF]-"
	// " RJ  Script Joe [ELF]"
	//@JsonManagedReference(value="playerDto")
	@JsonProperty(value = "winner")
	@Setter(AccessLevel.NONE)
	private PlayerDto winner;
	
	@JsonIgnore
	public GameType getGameTypeFromLabel(String gameType) throws Exception {
		GameType converted = GameType.fromLabel(gameType);
		if (converted == null) {
			throw new Exception("GameTypeParseLabelException");
		}
		return converted;
	}
	
	public void setGameType(GameType gameType) {
		// static Eum methods:
		// - valueOf() - returns enum instance taking a String
		// - values()  - returns all enum instances
		// instance Enum method:
		// - name()    - returns name of enum constant
		// -> better use toString() to get the user-friendly name
		this.gameType = gameType;
	}

	@JsonIgnore
	public CardGameStateMachine.State getStateConverted(String state) throws Exception {
		CardGameStateMachine.State converted = CardGameStateMachine.State.valueOf(state);
		if (converted == null) {
			throw new Exception("StateParseException");
		}
		return converted;
	}

	public void setState(CardGameStateMachine.State state) {
		// static Eum methods:
		// - valueOf() - returns enum instance taking a String
		// - values()  - returns all enum instances
		// instance Enum method:
		// - name()    - returns name of enum constant
		// -> better use toString() to get the user-friendly name

		this.state = state;
	}
	
	@JsonIgnore
	public Game getNameConverted(String name) {
		// "Highlow#0005 (Ante:100) [IS_SHUFFLED]"
		String[] splitName = StringUtils.split(StringUtils.remove(StringUtils.remove(StringUtils.remove(name, "Ante:"), "]"), " ["), "#()");
		
		if (splitName.length != 4 ||
				    splitName[0].isEmpty() || splitName[1].isEmpty() || splitName[2].isEmpty() || splitName[3].isEmpty()) {
			Game newGame = new Game();
			newGame.setGameType(GameType.fromLabel(splitName[0]));
			newGame.setGameId(Integer.parseInt(splitName[1]));
			newGame.setAnte(Integer.parseInt(splitName[2]));
			newGame.setState(CardGameStateMachine.State.valueOf(splitName[3]));
			return newGame;
		}
		
		Game newGame = new Game();
		newGame.setGameType(this.gameType);
		newGame.setGameId(this.gameId);
		newGame.setAnte(this.ante);
		newGame.setState(this.state);
		return newGame;
	}
	
	public void setName() {
		
		// "Highlow#0005 (Ante:100) [IS_SHUFFLED]"
		this.name = WordUtils.capitalizeFully(this.gameType.toString()) + "#" +
				            StringUtils.leftPad(String.valueOf(this.gameId), 4, "0") +
				            " (Ante:" + this.ante + ") [" +
				WordUtils.capitalizeFully(this.state.toString())
				+ "]";
	}
	
	public void setWinner(PlayerDto playerDto) {
		this.winner = playerDto;
	}
	
	public PlayerDto getWinner() {
		return this.winner;
	}
	
	
	public void setCardsDealt() {
		if (this.deckDtos != null) {
			StringBuilder sb = new StringBuilder(" card(s) [");
			List<DeckDto> decks;
			decks = this.deckDtos;
			// sort on card order
			Collections.sort(decks, Comparator.comparing(DeckDto::getCardOrder).thenComparing(DeckDto::getCardOrder));
			boolean first = true;
			int count = 0;
			
			for (DeckDto deck : decks) {
				
				if (!deck.getCardLocation().equals("STACK")) {
					count++;
					if (!first) {
						sb.append(" ");
					}
					first = false;
					sb.append(deck.getCardDto().getCardId());
				}
			}
			sb.append("]");
			sb.insert(0,count);
			
			this.dealt = sb.toString();
		} else {
			this.dealt = "0 cards []";
		}
	}
	
	public void setCardsLeft() {
		if (this.deckDtos != null) {
			StringBuilder sb = new StringBuilder(" card(s) [");
			List<DeckDto> decks;
			decks = this.deckDtos;
			// sort on card order
			Collections.sort(decks, Comparator.comparing(DeckDto::getCardOrder).thenComparing(DeckDto::getCardOrder));
			boolean first = true;
			int count = 0;
			for (DeckDto deck : decks) {
				if (deck.getCardLocation().equals("STACK")) {
					count++;
					if (!first) {
						sb.append(" ");
					}
					first = false;
					sb.append(deck.getCardDto().getCardId());
				}
			}
			sb.append("]");
			sb.insert(0,count);
			this.stock = sb.toString();
		} else {
			this.stock = "0 cards []";
		}
	}
	
	public void setDeckDtos(List<DeckDto> deckDtos) {
		this.deckDtos = deckDtos;
		this.setCardsDealt();
		this.setCardsLeft();
	}
	
	public List<DeckDto> getDeckDtos() {
		return this.deckDtos;
	}
}
