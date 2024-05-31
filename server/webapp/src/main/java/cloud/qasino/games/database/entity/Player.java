package cloud.qasino.games.database.entity;

import cloud.qasino.games.database.entity.enums.player.AiLevel;
import cloud.qasino.games.database.entity.enums.player.Avatar;
import cloud.qasino.games.database.entity.enums.player.Role;
import cloud.qasino.games.database.security.Visitor;
import com.fasterxml.jackson.annotation.*;
import com.voodoodyne.jackson.jsog.JSOGGenerator;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.*;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "player_id", nullable = false)
    private long playerId;

    @JsonIgnore
    @Column(name = "created", length = 25)
    private String created;


    // Foreign keys

    @JsonIgnore
    // UsPl: a Visitor can play many Games as a Player
    // However ai players are no visitors!
    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "visitor_id", referencedColumnName = "visitor_id", foreignKey = @ForeignKey
            (name = "fk_visitor_id"), nullable = true)
    private Visitor visitor;

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

    // current sequence of the player in the game, zero is a DECLINED VISITOR
    @Column(name = "seat")
    private int seat;

    @Enumerated(EnumType.STRING)
    @Column(name = "avatar", nullable = true, length = 50)
    private Avatar avatar;

    @Column(name = "avatar_name", nullable = true, length = 50)
    private String avatarName;

    @Enumerated(EnumType.STRING)
    @Column(name = "ai_level", nullable = true, length = 50)
    private AiLevel aiLevel;

    @Setter(AccessLevel.NONE)
    @Column(name = "is_winner")
    private boolean winner;

    // References
    @JsonIgnore
    // GaWi: one Player is the Winner of the GameSubTotals in the end
    @OneToOne(mappedBy = "player", cascade = CascadeType.DETACH)
    // just a reference the fk column is in "game" not here!
    private Result result;// = new Result();

    @JsonIgnore
    // HO: A Player holds one or more Card after dealing
    @OneToMany(mappedBy = "hand", cascade = CascadeType.DETACH)
    // just a reference, the actual fk column is in "game" not here !
    final private List<Card> cards = new ArrayList<>();

    public Player() {
        LocalDateTime localDateAndTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm-ssSSS-nnnnnnnnn");
        String result = localDateAndTime.format(formatter);
        this.created = result.substring(0, 20);
        this.human = true;
        this.winner = false;
        this.seat = 1;
    }

    public Player(Visitor visitor, Game game, Role role, int fiches, int seat, Avatar avatar, String avatarName, AiLevel aiLevel) {
        this();
        this.visitor = visitor;
        this.game = game;
        this.role = role;

        this.fiches = fiches;
        this.seat = seat;

        this.avatar = avatar;
        this.avatarName = avatarName;
        this.aiLevel = aiLevel;

        if (aiLevel == AiLevel.HUMAN) {
            this.human = true;
        } else {
            this.human = false;
        }
    }

    public static Player buildDummyBot(Game game, Avatar avatar, AiLevel aiLevel) {
        if (avatar == null) avatar = Avatar.GOBLIN;
        if (aiLevel == null) aiLevel = AiLevel.AVERAGE;
        return new Player(null, game, Role.BOT, 99, 99, avatar, "avatarName", aiLevel);
    }
    public static Player buildDummyHuman(Visitor visitor, Game game, Avatar avatar) {
        if (avatar == null) avatar = Avatar.GOBLIN;
        return new Player(visitor, game, Role.INITIATOR,99, 99, avatar, "avatarName", AiLevel.HUMAN);
    }
    public static Player buildDummyInvitee(Visitor visitor, Game game, Avatar avatar) {
        if (avatar == null) avatar = Avatar.GOBLIN;
        return new Player(visitor, game, Role.INVITED,99, 99, avatar, "avatarName", AiLevel.HUMAN);
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

    @Override
    public String toString() {
        return "(" +
                "playerId=" + this.playerId +
                ", visitorId=" + (this.visitor == null? "": this.visitor.getVisitorId()) +
                ", gameId=" + (this.game == null? "": this.game.getGameId()) +
                ", human=" + this.human +
                ", role=" + this.role == null ? "": this.role.getLabel() +
                ", fiches=" + this.fiches +
                ", seat=" + this.seat +
                ", avatar=" + this.avatar +
                ", aiLevel=" + this.aiLevel +
                ", winner=" + this.winner +
                ", resultId=" + (this.result == null? "": this.result.getResultId()) +
                ")";
    }

}

