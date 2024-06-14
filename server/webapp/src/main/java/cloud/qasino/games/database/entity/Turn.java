package cloud.qasino.games.database.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.voodoodyne.jackson.jsog.JSOGGenerator;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// @Data for JPA entities is an antipattern
@Entity
@DynamicUpdate
@Data // but we override equals, hash and toString and have noargs constructor
@JsonIdentityInfo(generator = JSOGGenerator.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "turn", indexes =
        {@Index(name = "turns_game_index", columnList = "game_id", unique = false)
         // not needed : @Index(name = "turns_index", columnList = "turn_id", unique = true)
        }
)
public class Turn {

    // @formatter:off

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "turn_id")
    private long turnId;
    @JsonIgnore
    @Column(name = "created", length = 25)
    private String updated;

    // Foreign keys
    // one [Turn] can be part of one [Game]
    @OneToOne (cascade = CascadeType.DETACH)
	@JoinColumn(name = "game_id", referencedColumnName = "game_id",foreignKey = @ForeignKey(name =
			"fk_game_id"), nullable=false)
	private Game game;

    // Derived functional fields
    @Column(name = "activePlayer_id", nullable = true)
    private long activePlayerId;
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
    // one [Turn] is parent for many [CardMoves]
    @OneToMany(mappedBy = "turn", cascade = CascadeType.DETACH)
    private List<CardMove> cardMoves = new ArrayList<>();

    public Turn() {
        setUpdated();
    }

    public Turn(Game game, long playerId) {
        this();
        this.game = game;
        this.activePlayerId = playerId;

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
        Turn turn = (Turn) o;
        return turnId == turn.turnId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(turnId);
    }

    @Override
    public String toString() {
        return "(" +
                "turnId=" + this.turnId +
                ", activePlayerId=" + (this.activePlayerId) +
                ", currentRoundNumber=" + this.currentRoundNumber +
                ", currentSeatNumber=" + this.currentSeatNumber +
                ", currentTurnNumber=" + this.currentMoveNumber +
                ", gameId=" + (this.game == null? "": this.game.getGameId()) +
                ", weekday=" + this.weekday +
                ")";
    }
}
