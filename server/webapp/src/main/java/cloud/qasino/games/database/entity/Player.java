package cloud.qasino.games.database.entity;

import cloud.qasino.games.database.entity.enums.player.AiLevel;
import cloud.qasino.games.database.entity.enums.player.Avatar;
import cloud.qasino.games.database.entity.enums.player.Role;
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
@Table(name = "player", indexes = {
        // not needed : @Index(name = "players_index", columnList = "player_id", unique = true)
})
public class Player {

    @Id
    @GeneratedValue
    @Column(name = "player_id")
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
    // PlGa: many Players can play the same GameSubTotals
    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "game_id", referencedColumnName = "game_id", foreignKey = @ForeignKey
            (name = "fk_game_id"), nullable = true)
    private Game game;


    // Normal fields

    @Setter(AccessLevel.NONE)
    @Column(name = "is_human")
    private boolean human;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private Role role;

    @Column(name = "fiches")
    private int fiches;

    // current sequence of the player in the game, zero is a DECLINED USER
    @Column(name = "seat")
    private int seat;

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

    // GaWi: one Player is the Winner of the GameSubTotals in the end
    @OneToOne(mappedBy = "player", cascade = CascadeType.DETACH)
    // just a reference the fk column is in game not here!
    private Result result;// = new Result();

    // HO: A Player holds one or more Card after dealing
    @OneToMany(mappedBy = "hand", cascade = CascadeType.DETACH)
    // just a reference, the actual fk column is in game not here !
    private List<Card> cards = new ArrayList<>();

    public Player() {
        LocalDateTime localDateAndTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm-ssSSS-nnnnnnnnn");
        String result = localDateAndTime.format(formatter);
        this.created = result.substring(2, 20);
        this.role = Role.INITIATOR;
        this.winner = false;
        this.human = true;
        this.seat = 1;
    }

    public Player(User user, Game game, Role role, int fiches, int seat) {
        this();
        this.user = user;
        this.role = role;
        this.game = game;
        this.human = this.user != null;
        this.fiches = fiches;
        this.seat = seat;
        this.aiLevel = AiLevel.HUMAN;
    }

    public Player(User user, Game game, Role role, int fiches, int seat, Avatar avatar, AiLevel aiLevel) {
        this(user, game, role, fiches, seat);

        this.avatar = avatar;
        this.aiLevel = aiLevel;
        if (aiLevel == AiLevel.HUMAN) {
            this.human = true;
        } else {
            this.human = false;
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
        } else {
            this.human = false;
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

