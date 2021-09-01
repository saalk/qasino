package cloud.qasino.quiz.entity;

import cloud.qasino.quiz.entity.Game;
import cloud.qasino.quiz.entity.Player;
import cloud.qasino.quiz.entity.enums.quiz.Face;
import cloud.qasino.quiz.entity.enums.quiz.Location;
import cloud.qasino.quiz.entity.enums.quiz.Position;
import com.fasterxml.jackson.annotation.*;
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
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class,
        property = "quizId")
//@JsonIdentityInfo(generator=JSOGGenerator.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "quizzes", indexes =
        { //@Index(name = "quizs_game_index", columnList = "game_id", unique = false ),
          @Index(name = "quizs_index", columnList = "quiz_id", unique = true )})
public class Quiz {
    
    @Id
    @GeneratedValue
    @Column(name = "quiz_id", nullable = false)
    private int quizId;

    @JsonIgnore
    @Column(name = "created", length = 25)
    private String created;

    @Column(name = "quiz", length = 3, nullable = false)
    private String quiz;

    // Foreign keys

    @JsonIgnore
    // SF: a shuffled Quiz is added to a GameSubTotals at the start
    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "game_id", referencedColumnName = "game_id", foreignKey = @ForeignKey(name =
            "fk_game_id"), nullable=false)
    private Game game;

    @JsonIgnore
    // HO: A Player sometimes holds a Quiz after dealing
    @OneToOne(optional=true, cascade = CascadeType.DETACH, fetch= FetchType.LAZY)
    @JoinColumn(name = "player_id", referencedColumnName = "player_id", foreignKey =
    @ForeignKey(name =
            "fk_player_id"), nullable=true)
    private Player hand;


    // Normal fields

    // current sequence of the quiz in the deck or hand
    @Column(name = "sequence")
    private int sequence;

    // current location for the quiz (can be hand or not)
    @Enumerated(EnumType.STRING)
    @Column(name = "location", nullable = false)
    private Location location;

    // current Postion for the quiz in the location
    @Enumerated(EnumType.STRING)
    @Column(name = "position", nullable = false)
    private Position position;

    // current Face for the quiz in the location (up or down)
    @Enumerated(EnumType.STRING)
    @Column(name = "face", nullable = false)
    private Face face;

    // References

    public Quiz() {
        LocalDateTime localDateAndTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm-ssSSS-nnnnnnnnn");
        String result = localDateAndTime.format(formatter);
        this.created = result.substring(2, 20);

    }

    public Quiz(String quiz, Game game, Player player, int sequence, Location location) {
        this();
        this.quiz = quiz;
        this.game = game;
        this.hand = player;
        this.sequence = sequence;
        this.location = location;
        this.position = Position.ORDERED;
        this.face = Face.DOWN;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quiz that = (Quiz) o;
        return quizId == that.quizId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(quizId);
    }

}
