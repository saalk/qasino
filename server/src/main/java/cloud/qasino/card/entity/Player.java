package cloud.qasino.card.entity;

import cloud.qasino.card.entity.enums.player.AiLevel;
import cloud.qasino.card.entity.enums.player.Avatar;
import com.fasterxml.jackson.annotation.*;
import com.voodoodyne.jackson.jsog.JSOGGenerator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@DynamicUpdate
@Getter
@Setter
@JsonIdentityInfo(generator= JSOGGenerator.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "players", indexes = {@Index(name = "players_index", columnList = "player_id",
        unique = true)})
public class Player {

    @Id
    @GeneratedValue
    @Column(name = "player_id", nullable = false)
    private int playerId;

    @JsonIgnore
    @Column(name = "created", length = 25)
    private String created;


    // Foreign keys

    // UsPl: a User can play many Games as a Player
    // However ai players are no users!
    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", foreignKey = @ForeignKey
            (name = "fk_user_id"), nullable = true)
    private User user;

    @JsonIgnore
    // PlGa: many Players can play the same Game
    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "game_id", referencedColumnName = "game_id", foreignKey = @ForeignKey
            (name = "fk_game_id"), nullable = true)
    private Game game;


    // Normal fields

    @Setter(AccessLevel.NONE)
    @Column(name = "is_human")
    private boolean human;

    @Setter(AccessLevel.NONE)
    @Column(name = "is_initiator")
    private boolean initiator; // todo must be enum INITIATOR, PENDING, ACCEPTED, BOT, DECLINED

    @Column(name = "fiches")
    private int fiches;

    // current sequence of the player in the game, zero is a DECLINED USER
    @Column(name = "sequence")
    private int sequence;

    @Enumerated(EnumType.STRING)
    @Column(name = "avatar", nullable = true, length = 50)
    private Avatar avatar;

    @Enumerated(EnumType.STRING)
    @Column(name = "ai_level", nullable = true, length = 50)
    private AiLevel aiLevel;

    @Setter(AccessLevel.NONE)
    @Column(name = "is_winner")
    private boolean winner;
    // References

/*    // GaWi: one Player is the Winner of the Game in the end
    @OneToOne(mappedBy = "winner", cascade = CascadeType.DETACH)
    // just a reference the fk column is in game not here!
    private Game winner = new Game();*/

    // HO: A Player holds one or more PlayingCard after dealing
    @OneToMany(mappedBy = "hand", cascade = CascadeType.DETACH)
    // just a reference, the actual fk column is in game not here !
    private List<PlayingCard> playingCards = new ArrayList<>();

    public Player() {
        LocalDateTime localDateAndTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm-ssSSS-nnnnnnnnn");
        String result = localDateAndTime.format(formatter);
        this.created = result.substring(2, 20);
        this.sequence = 1;
    }

    public Player(User user, Game game, int fiches, int sequence) {
        this();
        this.user = user;
        this.game = game;
        this.human = true;
        this.initiator = true;
        this.fiches = fiches;
        this.sequence = sequence;
        this.aiLevel = AiLevel.HUMAN;
    }

    public Player(User user, Game game, int fiches, int sequence, Avatar avatar, AiLevel aiLevel,
     boolean initiator) {
        this(user, game, fiches, sequence);

        this.avatar = avatar;
        this.aiLevel = aiLevel;
        if (aiLevel == AiLevel.HUMAN) {
            this.human = true;
            this.initiator = initiator;
        } else {
            this.human = false;
            this.initiator = false;
        }
        this.winner = false;
    }

    // todo LOW make unittest
    public boolean humanOrNot(AiLevel aiLevel) {
        return AiLevel.HUMAN.equals(aiLevel);
    }

    public void setAiLevel(AiLevel aiLevel) {
        this.aiLevel = aiLevel;
        if (aiLevel == AiLevel.HUMAN) {
            this.human = true;
            this.initiator = true;
        } else {
            this.human = false;
            this.initiator = false;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return playerId == player.playerId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId);
    }
}

