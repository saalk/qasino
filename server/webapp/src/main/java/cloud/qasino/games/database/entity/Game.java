package cloud.qasino.games.database.entity;

import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.database.entity.enums.card.PlayingCard;
import cloud.qasino.games.database.entity.enums.game.GameState;
import cloud.qasino.games.database.entity.enums.game.Style;
import cloud.qasino.games.database.entity.enums.game.Type;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.voodoodyne.jackson.jsog.JSOGGenerator;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@DynamicUpdate
@Data
@JsonIdentityInfo(generator = JSOGGenerator.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "game", indexes = {
        // not needed : @Index(name = "games_index", columnList = "game_id", unique = true)
})
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "game_id", nullable = false)
    private long gameId;

    @JsonIgnore
    @Column(name = "updated", length = 25, nullable = false)
    private String updated;


    // Foreign keys

    // TODO why json ignore??
    @JsonIgnore
    // PlGa: many Games can be part of the same League
    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "league_id", referencedColumnName = "league_id", foreignKey = @ForeignKey
            (name = "fk_league_id"), nullable = true)
    private League league;

    @Column(name = "initiator")
    private long initiator; // visitorId


    // Normal fields

    @Enumerated(EnumType.STRING)
    @Column(name = "state", length = 50, nullable = false)
    @Setter(AccessLevel.NONE)
    private GameState state;

    public void setState(GameState state) {
        this.previousState = this.state;
        this.state = state;
        setUpdated();
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_state", length = 50, nullable = true)
    private GameState previousState;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 50, nullable = false)
    private Type type;

    @Column(name = "style", length = 10, nullable = true)
    private String style;

    // A mandatory stake made before the "game" begins
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
    @Column(name = "weekday", length = 2)
    private int weekday;

    // References

    @JsonIgnore
    // SF: a shuffled Card is added to a GameSubTotals at the start
    @OneToMany(mappedBy = "game", cascade = CascadeType.DETACH)
    private List<Card> cards;

    // PlGa: many Players can play the same GameSubTotals
    @OneToMany(mappedBy = "game", cascade = CascadeType.DETACH)
    private List<Player> players;

    @JsonIgnore
    // AcTu: a Turn is kept do indicate the active player's move only
    @OneToOne(mappedBy = "game", cascade = CascadeType.DETACH)
    private Turn turn; // = new Turn();

    // todo delete
//    @OneToMany(mappedBy = "game", cascade = CascadeType.DETACH)
//    // just a reference, the actual fk column is in league not here!
//    private List<League> leagues;

    // just a reference, the actual fk column is in result not here!
    @OneToMany(mappedBy = "game", cascade = CascadeType.DETACH)
    private List<Result> results; // = new Result();

    public Game() {
        setUpdated();
        this.state = GameState.INITIALIZED;
        this.type = Type.HIGHLOW;
        this.style = new Style().getLabel();
        this.ante = 20;
    }

    public Game(League league, long initiator) {
        this();
        this.league = league;
        this.initiator = initiator;
    }

    public Game(League league, String type, long initiator) {
        this();
        this.league = league;
        this.type = Type.fromLabelWithDefault(type);
        this.initiator = initiator;
    }

    public Game(League league, String type, long initiator, String style, int ante) {
        this(league, type, initiator);
        this.style = Style.fromLabelWithDefault(style).getLabel();
        this.ante = ante;
    }

    private Game(Game.Builder builder) {
        this(builder.league, builder.type, builder.initiator, builder.style, builder.ante);
    }

    public static Game buildDummy(League league, long initiator) {
        return new Game.Builder()
                .withType(Type.HIGHLOW.getLabel())
                .withStyle("nrrn22")
                // ante, bet, deck, ins, rounds, turn
                .withAnte(20)
                .withInitiator(initiator)
                .withLeague(league)
                .build();
    }

    public static class Builder {
        private String type;
        private String style;
        private int ante;

        private long initiator;
        private League league;

        public Game.Builder withLeague(League league) {
            this.league = league;
            return this;
        }

        public Game.Builder withInitiator(long initiator) {
            this.initiator = initiator;
            return this;
        }

        public Game.Builder withStyle(String style) {
            this.style = style;
            return this;
        }

        public Game.Builder withType(String type) {
            this.type = type;
            return this;
        }

        public Game.Builder withAnte(int ante) {
            this.ante = ante;
            return this;
        }

        public Game build() {
            return new Game(this);
        }
    }

    public void setUpdated() {
        LocalDateTime localDateAndTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm-ssSSS-nnnnnnnnn");
        String result = localDateAndTime.format(formatter);
        this.updated = result.substring(0, 20);

        this.year = localDateAndTime.getYear();
        this.month = localDateAndTime.getMonth();
        DateTimeFormatter week = DateTimeFormatter.ofPattern("w");
        this.week = localDateAndTime.format(week);
        this.weekday = localDateAndTime.getDayOfMonth();
    }

    public void shuffleGame(int jokers) {

        List<PlayingCard> playingCards = PlayingCard.createDeckWithXJokers(jokers);
        Collections.shuffle(playingCards);
        int i = 1;
        for (PlayingCard playingCard : playingCards) {
            Card card = new Card(playingCard.getRankAndSuit(), this, null, i++, Location.STOCK);
            this.cards.add(card);
        }
    }

    // TODO LOW make this work with up / down and playerId
    // TODO error, a bot is no player !!!
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

    public List<Integer> getSeats() {
        return this.players.stream()
                .map(Player::getSeat)
                .sorted()
                .toList();
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
        return "(" +
                "gameId=" + this.gameId +
                ", leagueId=" + (this.league == null? "": this.league.getLeagueId()) +
                ", initiator=" + this.initiator +
                ", state=" + this.state +
                ", previousState=" + this.previousState +
                ", type=" + this.type +
                ", style=" + this.style +
                ", ante=" + this.ante +
                ", turnId=" + (this.turn == null? "": this.turn.getTurnId()) +
                ")";
    }
}

