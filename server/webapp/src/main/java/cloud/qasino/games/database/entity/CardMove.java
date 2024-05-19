package cloud.qasino.games.database.entity;

import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.database.entity.enums.move.Move;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.voodoodyne.jackson.jsog.JSOGGenerator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Entity
@DynamicUpdate
@Getter
@Setter
@JsonIdentityInfo(generator = JSOGGenerator.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "cardmove", indexes =
        {@Index(name = "cardmove_turn_index", columnList = "turn_id", unique = false),
                // not needed : @Index(name = "cardmove_index", columnList = "cardmove_id", unique = true)
        }
)
public class CardMove {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cardmove_id")
    private long cardMoveId;

    @JsonIgnore
    @Column(name = "created", length = 25)
    private String created;


    // Foreign keys

    @JsonIgnore
    // many cardmoves can be part of a turn
    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "turn_id", referencedColumnName = "turn_id", foreignKey = @ForeignKey
            (name = "fk_turn_id"), nullable = true)
    private Turn turn;

    @Column(name = "player_id")
    private long playerId;

    @Column(name = "card_id", nullable = true)
    private long cardId;

    // cardMove basics, what move does the player make
    @Enumerated(EnumType.STRING)
    @Column(name = "move", nullable = false)
    private Move move;


    // json
    @Column(name = "cardMove_details", nullable = true)
    private String cardMoveDetails;

    // json fields, filled in by engine
    @Setter(AccessLevel.NONE)
    @Column(name = "sequence")
    private String sequence;

    @Enumerated(EnumType.STRING)
    @Column(name = "location", nullable = true)
    private Location location;

    @Column(name = "bet", nullable = true)
    private int bet;
    @Column(name = "start_fiches", nullable = true)
    private int startFiches;
    @Column(name = "end_fiches", nullable = true)
    private int endFiches;

    // References

    public CardMove() {
        setCreated();
        setBet(0);
        setStartFiches(0);
        setEndFiches(0);
    }

    public CardMove(Turn turn, Player player, long cardId, Move move, Location location, String details) {
        this();
        this.turn = turn;
        this.playerId = player.getPlayerId();
        this.cardId = cardId;

        this.move = move;
        this.location = location;

        this.cardMoveDetails = details;
        this.sequence = "000000";
    }

    public void setCreated() {
        LocalDateTime localDateAndTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm-ssSSS-nnnnnnnnn");
        String result = localDateAndTime.format(formatter);
        this.created = result.substring(0, 20);
    }

    public void setSequence(int round, int seat, int turn) {
        // xxyyzz format
        this.sequence =
            String.format("%02d", round) +
            String.format("%02d", seat) +
            String.format("%02d", turn);
    }
    public int getRoundFromSequence() {
        return Integer.parseInt(this.sequence.substring(0,2));
    }
    public int getSeatFromSequence() {
        return Integer.parseInt(this.sequence.substring(2,4));
    }
    public int getMoveFromSequence() {
        return Integer.parseInt(this.sequence.substring(4,6));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CardMove cardMove = (CardMove) o;
        return cardMoveId == cardMove.cardMoveId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardMoveId);
    }

}
