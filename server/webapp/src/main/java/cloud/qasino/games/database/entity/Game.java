package cloud.qasino.games.database.entity;

import cloud.qasino.games.statemachine.GameState;
import cloud.qasino.games.database.entity.enums.game.Style;
import cloud.qasino.games.database.entity.enums.game.Type;
import cloud.qasino.games.database.entity.enums.card.PlayingCard;
import cloud.qasino.games.database.entity.enums.card.Location;
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
    @Column(name = "updated", length = 25, nullable = false)
    private String updated;


    // Foreign keys

    @JsonIgnore
    // PlGa: many Games can be part of the same League
    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "league_id", referencedColumnName = "league_id", foreignKey = @ForeignKey
            (name = "fk_league_id"), nullable = true)
    private League league;

    @Column(name = "initiator")
    private int initiator; // userId


    // Normal fields

    @Enumerated(EnumType.STRING)
    @Column(name = "state", length = 50, nullable = false)
    private GameState state;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_state", length = 50, nullable = true)
    private GameState previousState;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 50, nullable = false)
    private Type type;

    @Column(name = "style", length = 10, nullable = true)
    private String style;

    // A mandatory stake made before the game begins
    @Column(name = "ante")
    private int ante;


    // Derived fields

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
    // SF: a shuffled Card is added to a GameSubTotals at the start
    @OneToMany(mappedBy = "game", cascade = CascadeType.DETACH)
    private List<Card> cards = new ArrayList<>();

    // PlGa: many Players can play the same GameSubTotals
    @OneToMany(mappedBy = "game", cascade = CascadeType.DETACH)
    private List<Player> players = new ArrayList<>();

    @JsonIgnore
    // AcTu: a Turn is kept do indicate the active player's move only
    @OneToOne(mappedBy = "game", cascade = CascadeType.DETACH)
    private Turn turn; // = new Turn();

    @JsonIgnore
    // AcTu:
    // a Turn is kept do indicate the active player's move only
    @OneToOne(mappedBy = "game", cascade = CascadeType.DETACH)
    private Result result; // = new Result();

    public Game() {
        setUpdated();
        this.state = GameState.NEW;
        this.type = Type.HIGHLOW;
        this.style = new Style().getLabel();
        this.ante = 20;
    }

    public Game(League league, String type, int initiator ) {
        this();
        this.league = league;
        this.type = Type.fromLabelWithDefault(type);
        this.initiator = initiator;
    }

    public Game(League league, String type, int initiator, String style, int ante) {
        this(league, type, initiator);
        this.style = Style.fromLabelWithDefault(style).getLabel();
        this.ante = ante;
    }

    public void setUpdated() {
        LocalDateTime localDateAndTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm-ssSSS-nnnnnnnnn");
        String result = localDateAndTime.format(formatter);
        this.updated = result.substring(2, 20);

        this.year = localDateAndTime.getYear();
        this.month = localDateAndTime.getMonth();
        DateTimeFormatter week = DateTimeFormatter.ofPattern("W");
        this.week = localDateAndTime.format(week);
        this.day = localDateAndTime.getDayOfMonth();
    }

    public void shuffleGame(int jokers) {

        List<PlayingCard> playingCards = PlayingCard.newDeck(jokers);
        Collections.shuffle(playingCards);

        int i = 1;
        for (PlayingCard playingCard : playingCards) {
            Card card = new Card(playingCard.getCardId(), this, null, i++, Location.STOCK );
            this.cards.add(card);
        }
    }

    // todo LOW make this work with up / down and playerId
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
        if ((cyclePlayer.getSeat() == 1) && (playingOrderChanged) && (moveTowardsFirst)) {
            playingOrderChanged = false;
        } else if ((cyclePlayer.getSeat() == players.size()) && (playingOrderChanged) && (moveTowardsLast)) {
            playingOrderChanged = false;
        }

        if (playingOrderChanged) {
            // do the switch
            int oldPlayingOrder = cyclePlayer.getSeat();
            // update the current
            int newPlayingOrder = moveTowardsFirst ? (cyclePlayer.getSeat() - 1) :
                    (cyclePlayer.getSeat() + 1);
            // find the other that is currently on the newPlayingOrder
            cycledPlayer = this.players.get(newPlayingOrder);
            cycledPlayer.setSeat(oldPlayingOrder);
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
