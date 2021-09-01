package cloud.qasino.quiz.entity;

import cloud.qasino.quiz.entity.Player;
import cloud.qasino.quiz.entity.Turn;
import cloud.qasino.quiz.entity.enums.quiz.Location;
import cloud.qasino.quiz.entity.enums.move.Move;
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
import java.util.Objects;

@Entity
@DynamicUpdate
@Getter
@Setter
@JsonIdentityInfo(generator = JSOGGenerator.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "quizmove", indexes =
        {@Index(name = "quizmove_turn_index", columnList = "turn_id", unique = false),
                @Index(name = "quizmove_index", columnList = "quizmove_id", unique = true)}
)
public class QuizMove {

    @Id
    @GeneratedValue
    @Column(name = "quizmove_id")
    private int quizMoveId;

    @JsonIgnore
    @Column(name = "created", length = 25)
    private String created;


    // Foreign keys

    @JsonIgnore
    // many quizmoves can be part of a turn
    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "turn_id", referencedColumnName = "turn_id", foreignKey = @ForeignKey
            (name = "fk_turn_id"), nullable = true)
    private cloud.qasino.quiz.entity.Turn turn;

    @Column(name = "player_id", nullable = false)
    private int playerId;

    @Column(name = "quiz_id", nullable = true)
    private String quizId;


    // quizMove basics, what move does the player make

    @Enumerated(EnumType.STRING)
    @Column(name = "move", nullable = false)
    private Move move;


    // json
    @Column(name = "quizMove_details", nullable = true)
    private String quizMoveDetails;

    // json fields, filled in by engine
    @Column(name = "round_number", nullable = false)
    private int roundNumber;

    @Column(name = "move_number", nullable = false)
    private int moveNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "location", nullable = true)
    private Location location;

    @Column(name = "bet", nullable = true)
    private int bet;

    // References


    public QuizMove() {
        setCreated();
    }

    public QuizMove(Turn turn, Player player, String quizId, Move move, Location location) {
        this();
        this.turn = turn;
        this.playerId = player.getPlayerId();
        this.quizId = quizId;

        this.move = move;
        this.location = location;
    }

    public void setCreated() {
        LocalDateTime localDateAndTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm-ssSSS-nnnnnnnnn");
        String result = localDateAndTime.format(formatter);
        this.created = result.substring(2, 20);

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuizMove quizMove = (QuizMove) o;
        return quizMoveId == quizMove.quizMoveId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(quizMoveId);
    }

}
