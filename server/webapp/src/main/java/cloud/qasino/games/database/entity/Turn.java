package cloud.qasino.games.database.entity;

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
import java.util.List;
import java.util.Objects;

@Entity
@DynamicUpdate
@Getter
@Setter
@JsonIdentityInfo(generator = JSOGGenerator.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "turn", indexes =
        {@Index(name = "turns_game_index", columnList = "game_id", unique = false)
         // not needed : @Index(name = "turns_index", columnList = "turn_id", unique = true)
        }
)
public class Turn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "turn_id")
    private long turnId;

    @JsonIgnore
    @Column(name = "created", length = 25)
    private String updated;


    // Foreign keys

    // AcTu: a Turn is kept do indicate the active player's move in a Game only
    @OneToOne (cascade = CascadeType.DETACH)
	@JoinColumn(name = "game_id", referencedColumnName = "game_id",foreignKey = @ForeignKey(name =
			"fk_game_id"), nullable=false)
	private Game game;


    // Derived functional fields


    // can be player objet instead of id
    @Column(name = "activePlayer_id", nullable = true)
    private long activePlayerId;

    @Column(name = "current_round_number", nullable = true)
    private int currentRoundNumber;

    @Column(name = "current_move_number", nullable = true)
    private int currentMoveNumber;


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


    // References

    // L: A turn can have many moves over time
    @OneToMany(mappedBy = "turn", cascade = CascadeType.DETACH)
    // just a reference, the actual fk column is in game not here !
    private List<CardMove> cardMoves = new ArrayList<>();


    public Turn() {
        setUpdated();
    }

    public Turn(Game game, long playerId) {
        this();
        this.game = game;
        this.activePlayerId = playerId;

        this.currentRoundNumber = 1;
        this.currentMoveNumber = 1;

    }

    public void setUpdated() {
        LocalDateTime localDateAndTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm-ssSSS-nnnnnnnnn");
        String result = localDateAndTime.format(formatter);
        this.updated = result.substring(0, 20);

        this.year = localDateAndTime.getYear();
        this.month = localDateAndTime.getMonth();
        DateTimeFormatter week = DateTimeFormatter.ofPattern("W");
        this.week = localDateAndTime.format(week);
        this.weekday = localDateAndTime.getDayOfMonth();
    }

    public void newTurn(long newPlayerId) {
        setUpdated();
        this.activePlayerId = newPlayerId;
        this.currentRoundNumber = ++ currentRoundNumber;
        this.currentMoveNumber = ++ currentMoveNumber;
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

}
