package cloud.qasino.card.entity;

import cloud.qasino.card.domain.qasino.Card;
import cloud.qasino.card.domain.qasino.Style;
import cloud.qasino.card.entity.enums.Location;
import cloud.qasino.card.entity.enums.Type;
import cloud.qasino.card.statemachine.QasinoStateMachine;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.voodoodyne.jackson.jsog.JSOGGenerator;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@DynamicUpdate
@Getter
@Setter
@JsonIdentityInfo(generator = JSOGGenerator.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
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
/*	@OneToOne (cascade = CascadeType.DETACH)
	@JoinColumn(name = "player_id", referencedColumnName = "player_id",foreignKey = @ForeignKey(name =
			"fk_player_id"), nullable=true)
	private Player winner;*/


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

    @JsonIgnore
    // SF: a shuffled PlayingCard is added to a Game at the start
    @OneToMany(mappedBy = "game", cascade = CascadeType.DETACH)
    private List<PlayingCard> playingCards = new ArrayList<>();

    // PlGa: many Players can play the same Game
    @OneToMany(mappedBy = "game", cascade = CascadeType.DETACH)
    private List<Player> players = new ArrayList<>();

    public Game() {
        LocalDateTime localDateAndTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm-ssSSS-nnnnnnnnn");
        String result = localDateAndTime.format(formatter);
        this.created = result.substring(2, 20);

        this.state = QasinoStateMachine.GameState.INITIALIZED;
        this.type = Type.HIGHLOW;
        this.style = new Style().getLabel();
        this.ante = 20;
    }

    public Game(Type type) {
        this();

        this.type = type;
    }

    public Game(Type type, String style, int ante) {
        this(type);

        this.style = Style.fromLabelWithDefault(style).getLabel();
        this.ante = ante;
    }

    public void addDeck(int jokers) {

        if (this.playingCards.size() > 0)
                return this;

        List<Card> cards = Card.newDeck(jokers);
        int i = 1;
        for (Card card : cards) {
            PlayingCard playingCard = new PlayingCard(card.getCardId(),this, null, i++, Location.PILE);
            this.playingCards.add(playingCard);
        }
        //return this;
    }

    public Game shuffleGame(Game game) {

        Collections.shuffle(game.playingCards);
        return game;
    }

    public boolean switchPlayers (int sequence, int direction) {

        // check if playing order is up (-1) or down (+1)
        boolean playingOrderChanged = false;
        boolean moveTowardsFirst = false;
        boolean moveTowardsLast = false;

        if (sequence == 0 || sequence > this.players.size() || !(direction == -1 | direction == 1)) {
            playingOrderChanged = false;
        } else if (direction == -1) {
            moveTowardsFirst = true;
            playingOrderChanged = true;
        } else if (direction == 1) {
            moveTowardsLast = true;
            playingOrderChanged = true;
        } else {
            playingOrderChanged = false;
            moveTowardsFirst = false;
            moveTowardsLast = false;
        }

        Player cyclePlayer = this.players.get(sequence);
        Player cycledPlayer;

        // see if the change can be done
        if ((cyclePlayer.getSequence() == 1) && (playingOrderChanged) && (moveTowardsFirst)) {
            playingOrderChanged = false;
        } else if ((cyclePlayer.getSequence() == players.size()) && (playingOrderChanged) && (moveTowardsLast)) {
            playingOrderChanged = false;
        }

        if (playingOrderChanged) {
            // do the switch
            Integer oldPlayingOrder = cyclePlayer.getSequence();
            // update the current
            Integer newPlayingOrder = moveTowardsFirst ? (cyclePlayer.getSequence() - 1) :
                    (cyclePlayer.getSequence() + 1);
            // find the other that is currently on the newPlayingOrder
            cycledPlayer = this.players.get(newPlayingOrder);
            cycledPlayer.setSequence(oldPlayingOrder);
        }
        return playingOrderChanged;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return gameId == game.gameId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameId);
    }

    @Override
    public String toString() {
        return "Game{" +
                "gameId=" + gameId +
                ", created='" + created + '\'' +
                ", state=" + state +
                ", type=" + type +
                ", style='" + style + '\'' +
                ", ante=" + ante +
                ", playingCards=" + playingCards +
                ", players=" + players +
                '}';
    }
}

