package cloud.qasino.card.entity;

import cloud.qasino.card.entity.enums.CardLocation;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import cloud.qasino.card.entity.enums.GameType;
import cloud.qasino.card.statemachine.CardGameStateMachine;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Entity
@DynamicUpdate
@Table(name = "GAME", indexes = {@Index(name = "GAME_INDEX", columnList = "GAME_ID")})
@Getter
@Setter
//@Relation(value = "game", collectionRelation = "gameDtos")
//@JsonIdentityInfo(generator=JSOGGenerator.class)
// - this annotation adds @Id to prevent chain loop
// - you could also use @JsonManagedReference and @JsonBackReference
public class Game implements Serializable {
	
	// 14 fields
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	@Column(name = "GAME_ID")
	private int gameId;
	@Column(name = "CREATED", length = 25)
	private String created;
	@Enumerated(EnumType.STRING)
	@Column(name = "STATE", length = 50, nullable = false)
	private CardGameStateMachine.State state;
	@Enumerated(EnumType.STRING)
	@Column(name = "GAMETYPE", length = 50, nullable = false)
	private GameType gameType;
	@Column(name = "GAMEVARIANT", length = 50, nullable = false)
	private String gameVariant;
	@Column(name = "ACTIVE_CASINO")
	private int activeCasino;
	@Column(name = "CURRENT_ROUND")
	private int currentRound;
	@Column(name = "ANTE")
	private int ante;
	
	// Cascade = any change happened on this entity must cascade to the parent/child as well
	// since this is the parent Game: do all when Game is delete on the deck childs
	// meaning do set cascade type to all -> changed to delete not create
	//@JsonIgnore
	@OneToMany(cascade = CascadeType.REMOVE, fetch= FetchType.LAZY)
	@JoinColumn(name = "GAME_ID", referencedColumnName = "GAME_ID", foreignKey = @ForeignKey(name = "GAME_ID"))
	private List<Deck> decks;
	
	// Cascade = any change happened on this entity must cascade to the parent/child as well
	// since this is the parent Game: do all when Game is delete on the deck childs
	// meaning do set cascade type to all -> changed to delete not create
	//@JsonIgnore
	@OneToMany(cascade = CascadeType.REMOVE, fetch= FetchType.LAZY)
	@JoinColumn(name = "GAME_ID", referencedColumnName = "GAME_ID", foreignKey = @ForeignKey(name = "GAME_ID"))
	private List<Casino> casinos;
	
	// Cascade = any change happened on this entity must cascade to the parent/child as well
	// since this is the child Game: do nothing when Game is delete on the winner Player
	// meaning do not set cascade options
	//@JsonIgnore
	@ManyToOne(optional=true, fetch= FetchType.LAZY)
	@JoinColumn(name = "PLAYER_ID", referencedColumnName = "PLAYER_ID",foreignKey = @ForeignKey(name = "PLAYER_ID"))
	private Player player;
	
	/*// make a lookup from all the STATES
	private static final Map<String, State> lookup
			= new HashMap<>();
	
	static {
		for (State state : EnumSet.allOf(State.class))
			lookup.put(state.toString(), state);
	}
	
	@JsonIgnore
	public State getStateFromString() {
		return lookup.get(state); // lookup is a Map<String, State>
	}
*/
	@JsonIgnore
	public int increaseCurrentRound() {
		this.currentRound++;
		return currentRound;
	}
	
	public Game() {
		LocalDateTime localDateAndTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm-ssSSS-nnnnnnnnn");
		String result = localDateAndTime.format(formatter);
		this.created = result.substring(2, 20);
	}
	
	public void addShuffledDeckToGame(int jokers) {
		
		List<Card> cards = Card.newDeck(jokers);
		Collections.shuffle(cards);
		
		int i = 1;
		for (Card card : cards) {
			Deck deck = new Deck();
			deck.setCard(card);
			deck.setCardOrder(i++);
			deck.setDealtTo(null);
			deck.setCardLocation(CardLocation.STACK);
			this.decks.add(deck);
		}
		
	}
	
}

