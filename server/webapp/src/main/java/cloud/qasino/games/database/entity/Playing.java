package cloud.qasino.games.database.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.voodoodyne.jackson.jsog.JSOGGenerator;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// @Entity creates a direct link between class object(s) and table row(s)
@Entity
// @DynamicUpdate includes only columns which are actually being updated - not the cached insert
@DynamicUpdate
// @Data for JPA entities is an antipattern
// But we override equals, hash and toString and have noargs constructor.
@Data
@JsonIdentityInfo(generator = JSOGGenerator.class, property = "turnId")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "playing"
//        , indexes =
//        {@Index(name = "playing_game_index", columnList = "game_id", unique = false)
//          not needed : @Index(name = "turns_index", columnList = "playing_id", unique = true)
//        }
)
public class Playing {

    // @formatter:off
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "playing_id")
    private long playingId;
    @JsonIgnore
    @Column(name = "created", length = 25)
    private String updated;

    // Foreign keys
    // one [Playing] can be part of one [Game]
    @OneToOne (cascade = CascadeType.DETACH)
	@JoinColumn(name = "game_id", referencedColumnName = "game_id",foreignKey = @ForeignKey(name =
			"fk_game_id"), nullable=false)
	private Game game;

    // one [Player] can be part of one [Playing]
    @OneToOne (cascade = CascadeType.DETACH)
    @JoinColumn(name = "player_id", referencedColumnName = "player_id",foreignKey = @ForeignKey(name =
            "fk_player_id"), nullable=false)
    private Player player;

    // Functional fields
    @Column(name = "current_round_number", nullable = true)
    private int currentRoundNumber;
    @Column(name = "current_seat_number", nullable = true)
    private int currentSeatNumber;
    @Column(name = "current_move_number", nullable = true)
    @Setter(AccessLevel.NONE)
    private int currentMoveNumber;
    public void setCurrentMoveNumber(int currentMoveNumber) {
        this.currentMoveNumber = currentMoveNumber;
        setUpdated();
    }

    // Derived technical fields
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

    // References - the actual FK are in other tables
    // one [Playing] is parent for many [CardMoves]
    @OneToMany(mappedBy = "playing", cascade = CascadeType.DETACH)
    private List<CardMove> cardMoves = new ArrayList<>();

    public Playing() {
        setUpdated();
    }

    public Playing(Game game, Player player) {
        this();
        this.game = game;
        this.player = player;

        this.currentRoundNumber = 1;
        this.currentSeatNumber = 1;
        this.currentMoveNumber = 1;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Playing playing = (Playing) o;
        return playingId == playing.playingId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(playingId);
    }

    @Override
    public String toString() {
        return "(" +
                "playingId=" + this.playingId +
                ", playerId=" + (this.player.getPlayerId()) +
                ", player avatarName=" + (this.player.getAvatarName()) +
                ", currentRoundNumber=" + this.currentRoundNumber +
                ", currentSeatNumber=" + this.currentSeatNumber +
                ", currentMoveNumber=" + this.currentMoveNumber +
                ", cardMoves count=" + this.cardMoves.size() +
                ", gameId=" + (this.game == null? "": this.game.getGameId()) +
                ", game state=" + (this.game == null? "": this.game.getState()) +
                ", weekday=" + this.weekday +
                ")";
    }
}
