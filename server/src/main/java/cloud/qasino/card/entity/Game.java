package cloud.qasino.card.entity;

import cloud.qasino.card.domain.qasino.Card;
import cloud.qasino.card.domain.qasino.Style;
import cloud.qasino.card.domain.qasino.statemachine.GameState;
import cloud.qasino.card.entity.enums.game.Type;
import cloud.qasino.card.entity.enums.playingcard.Location;
import cloud.qasino.card.statemachine.QasinoStateMachine;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.voodoodyne.jackson.jsog.JSOGGenerator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.Month;
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

    @JsonIgnore
    @Column(name = "created", length = 25, nullable = false)
    private String created;


    // Foreign keys

    // GaWi: a game has a winner in the end
/*	@OneToOne (cascade = CascadeType.DETACH)
	@JoinColumn(name = "player_id", referencedColumnName = "player_id",foreignKey = @ForeignKey(name =
			"fk_player_id"), nullable=true)
	private Player winner;*/

    @JsonIgnore
    // PlGa: many Games can be part of the same League
    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "league_id", referencedColumnName = "league_id", foreignKey = @ForeignKey
            (name = "fk_league_id"), nullable = true)
    private League league;

    // Normal fields

    @Enumerated(EnumType.STRING)
    @Column(name = "state", length = 50, nullable = false)
    private GameState state;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 50, nullable = false)
    private Type type;

    @Column(name = "style", length = 10, nullable = true)
    private String style;

    @Column(name = "ante")
    private int ante;

    @Setter(AccessLevel.NONE)
    @Column(name = "year", length = 4)
    private int year;

    @Setter(AccessLevel.NONE)
    @Column(name = "month", length = 20)
    private Month month;

    @Setter(AccessLevel.NONE)
    @Column(name = "week", length = 3)
    private String week;

    @Setter(AccessLevel.NONE)
    @Column(name = "day", length = 2)
    private int day;

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

        this.year = localDateAndTime.getYear();
        this.month = localDateAndTime.getMonth();
        DateTimeFormatter week = DateTimeFormatter.ofPattern("W");
        this.week = localDateAndTime.format(week);
        this.day = localDateAndTime.getDayOfMonth();

        this.state = GameState.INITIALIZED;
        this.type = Type.HIGHLOW;
        this.style = new Style().getLabel();
        this.ante = 20;
    }

    public Game(League league, String type) {
        this();
        this.league = league;
        this.type = Type.fromLabelWithDefault(type);
    }

    public Game(League league, String type, String style, int ante) {
        this(league,type);
        this.style = Style.fromLabelWithDefault(style).getLabel();
        this.ante = ante;
    }

    public void shuffleGame(int jokers) {

        List<Card> cards = Card.newDeck(jokers);
        Collections.shuffle(cards);

        int i = 1;
        for (Card card : cards) {
            PlayingCard playingCard = new PlayingCard(card.getCardId(),this, null, i++, Location.PILE);
            this.playingCards.add(playingCard);
        }
    }

    public boolean switchPlayers(int sequence, int direction) {

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

}

