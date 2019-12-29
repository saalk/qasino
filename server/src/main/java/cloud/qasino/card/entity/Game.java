package cloud.qasino.card.entity;

import cloud.qasino.card.domain.qasino.Card;
import cloud.qasino.card.entity.enums.Move;
import cloud.qasino.card.entity.enums.Type;
import cloud.qasino.card.statemachine.QasinoStateMachine;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
//@Inheritance(strategy = InheritanceType.JOINED)
@Data
@Table(name = "games", indexes = {@Index(name = "games_index", columnList = "game_id", unique =
		true)})
public class Game {
	
	@Id
	@GeneratedValue
	@Column(name = "game_id", nullable = false)
	private int gameId;

	@Column(name = "created", length = 25, nullable = false)
	private String created;


	// Foreign keys

	// GaWi: a game has a winner in the end
	@OneToOne (cascade = CascadeType.DETACH)
	@JoinColumn(name = "player_id", referencedColumnName = "player_id",foreignKey = @ForeignKey(name =
			"fk_player_id"), nullable=true)
	private Player winner;


	// Normal fields

	@Enumerated(EnumType.STRING)
	@Column(name = "state", length = 50, nullable = false)
	private QasinoStateMachine.GameState state;

	@Enumerated(EnumType.STRING)
	@Column(name = "type", length = 50, nullable = false)
	private Type type;

	@Column(name = "style", length = 10, nullable = true)
	private String style;

	@Column(name = "ante")
	private int ante;


	// References

	// SF: a shuffled PlayingCard is added to a Game at the start
	@OneToMany(mappedBy = "game", cascade = CascadeType.DETACH)
	private List<PlayingCard> playingCards = new ArrayList<>();

	// PlGa: many Players can play the same Game
	@OneToMany(mappedBy = "plays", cascade = CascadeType.DETACH)
	private List<Player> players = new ArrayList<>();

	public Game() {
	}

	public Game(Type type) {
		this();
		LocalDateTime localDateAndTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm-ssSSS-nnnnnnnnn");
		String result = localDateAndTime.format(formatter);
		this.created = result.substring(2, 20);
		this.state = QasinoStateMachine.GameState.INITIALIZED;
		this.type = type;
	}

	public void addShuffledPlayingCardToGame(Game game, int jokers) {
		
		List<Card> cards = Card.newDeck(jokers);
		Collections.shuffle(cards);
		
		int i = 1;
		for (Card card : cards) {
			PlayingCard playingCard = new PlayingCard();
			playingCard.setGame(game);
			playingCard.setHand(null);

			playingCard.setSequence(i++);
			playingCard.setMove(Move.PILE_DRAW);
		}
	}

	@Override
	public String toString() {
		return "Game{" +
				"gameId=" + gameId +
				", created='" + created + '\'' +
				// fk
				", winner=" + winner.getPlayerId() +
				// fields
				", state=" + state.name() +
				", type=" + type.toString() +
				", style=" + style +
				", ante=" + ante +
				'}';
	}
}

