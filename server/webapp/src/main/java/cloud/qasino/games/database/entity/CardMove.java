package cloud.qasino.games.database.entity;

import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.database.entity.enums.move.Move;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.voodoodyne.jackson.jsog.JSOGGenerator;
import lombok.AccessLevel;
import lombok.Data;
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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

// @Entity creates a direct link between class object(s) and table row(s)
@Entity
// @DynamicUpdate includes only columns which are actually being updated - not the cached insert
@DynamicUpdate
// @Data for JPA entities is an antipattern
// But we override equals, hash and toString and have noargs constructor.
@Data
@JsonIdentityInfo(generator = JSOGGenerator.class, property = "cardMoveId")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "cardmove", indexes = {
//        @Index(name = "cardmove_playing_index", columnList = "playing_id", unique = false),
                // not needed : @Index(name = "cardmove_index", columnList = "cardmove_id", unique = true)
        }
)
public class CardMove {

    // @formatter:off
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cardmove_id", nullable = false)
    private long cardMoveId;
    @JsonIgnore
    @Column(name = "created", length = 25)
    private String created;

    // Foreign keys
    @JsonIgnore
    // many [CardMove] are part of one [Playing]
    @ManyToOne
    @JoinColumn(name = "playing_id", referencedColumnName = "playing_id", foreignKey = @ForeignKey
            (name = "fk_playing_id"), nullable = false)
    private Playing playing;
    // one [CardMove] can be part of one [Player]
    @Column(name = "player_id")
    private long playerId;
    // TODO move cards can be in one move in future
    // one [CardMove] can be part of one [Card]
    @Column(name = "card_id", nullable = true)
    private long cardId;

    // Normal fields
    @Enumerated(EnumType.STRING)
    @Column(name = "move", nullable = false)
    private Move move;
    @Column(name = "cardMove_details", nullable = true)
    private String cardMoveDetails;
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

    // References - the actual FK are in other tables
    // none

    public CardMove() {
        setCreated();
        setBet(0);
        setStartFiches(0);
        setEndFiches(0);
    }

    public CardMove(Playing playing, Player player, long cardId, Move move, Location location, String details) {
        this();
        this.playing = playing;
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

    public void setSequence(int round, int seat, int move) {
        // xxyyzz format
        this.sequence =
            String.format("%02d", round) +
            String.format("%02d", seat) +
            String.format("%02d", move);
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

    @Override
    public String toString() {
        return "(" +
                "cardMoveId=" + this.cardMoveId +
                ", playingId=" + this.playing.getPlayingId() +
                ", playerId=" + this.playerId +
                ", cardId=" + this.cardId +
                ", move=" + this.move.getLabel() +
                ", cardMoveDetails=" + this.cardMoveDetails +
                ", sequence=" + this.sequence +
                ", location=" + (this.location.getLabel()) +
                ", bet=" + (this.bet) +
                ", startFiches=" + this.startFiches +
                ", endFiches=" + this.endFiches +
                ")";
    }
}
