package cloud.qasino.card.entity;

import cloud.qasino.card.entity.enums.CardLocation;
import cloud.qasino.card.entity.enums.GameType;
import cloud.qasino.card.statemachine.QasinoStateMachine;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Entity
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@Table(name = "games", indexes = {@Index(name = "games_index", columnList = "gameId")})
public class Game {
	
	// 14 fields
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private int gameId;

	@Column(length = 25)
	private String created;

	@Enumerated(EnumType.STRING)
	@Column(length = 50, nullable = false)
	private QasinoStateMachine.State state;

	@Enumerated(EnumType.STRING)
	@Column(length = 50, nullable = false)
	private GameType gameType;

	@Column(length = 50, nullable = true)
	private GameVariant gameVariant;

	private int ante;
	
	// Cascade = any change happened on this entity must cascade to the parent/child as well
	// since this is the child Game: do nothing when Game is delete on the winner Player
	// meaning do not set cascade options
	//@JsonIgnore
	@OneToOne (optional=true, fetch= FetchType.LAZY)
	@JoinColumn(name = "playerId", referencedColumnName = "playerId",foreignKey =
	@ForeignKey(name = "playerId"))
	private Player winner;

	public Game(GameType gameType) {
		LocalDateTime localDateAndTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm-ssSSS-nnnnnnnnn");
		String result = localDateAndTime.format(formatter);
		this.created = result.substring(2, 20);
		this.state = QasinoStateMachine.State.SELECTED;
		this.gameType = gameType;
	}

	public void addShuffledDeckToGame(Game game, int jokers) {
		
		List<Card> cards = Card.newDeck(jokers);
		Collections.shuffle(cards);
		
		int i = 1;
		for (Card card : cards) {
			Deck deck = new Deck();
			deck.setGame(game);
			deck.setDealtTo(null);
			deck.setCard(card);

			deck.setCardOrder(i++);
			deck.setCardLocation(CardLocation.STACK);
		}
	}

	@Override
	public String toString() {
		return "Game{" +
				"gameId=" + gameId +
				", created='" + created + '\'' +
				", state=" + state.name() +
				", gameType=" + gameType.toString() +
				", gameVariant=" + gameVariant.toString() +
				", ante=" + ante +
				", winner=" + winner.getPlayerId() +
				'}';
	}
}

