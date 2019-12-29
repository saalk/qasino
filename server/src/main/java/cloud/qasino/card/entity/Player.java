package cloud.qasino.card.entity;

import cloud.qasino.card.entity.enums.AiLevel;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name = "players", indexes = {@Index(name = "players_index", columnList = "player_id",
        unique = true)})
public class Player {

    @Id
    @GeneratedValue
    @Column(name = "player_id", nullable = false)
    private int playerId;

    @Column(name = "created", length = 25)
    private String created;


    // Foreign keys

    // UsPl: a User can play many Games as a Player
    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", foreignKey = @ForeignKey
     (name = "fk_user_id"), nullable=false)
    private User user;

    // PlGa: many Players can play the same Game
    @ManyToOne (cascade = CascadeType.DETACH)
    @JoinColumn(name = "game_id", referencedColumnName = "game_id", foreignKey = @ForeignKey
            (name = "fk_game_id"), nullable=false)
    private Game plays;


    // Normal fields

    @Setter(AccessLevel.NONE)
    @Column(name = "is_human")
    private boolean human;

    //@Enumerated(EnumType.STRING)
    @Column(name = "avatar", nullable = true, length = 50)
    private String avatar;

    @Enumerated(EnumType.STRING)
    @Column(name = "ai_level", nullable = true, length = 50)
    private AiLevel aiLevel;


    // References

    // GaWi: one Player is the Winner of the Game in the end
    @OneToOne (mappedBy="winner", cascade = CascadeType.DETACH)
    // just a reference the fk column is in game not here!
    private Game game = new Game();

    // HO: A Player holds one or more PlayingCard after dealing
    @OneToMany (mappedBy="hand", cascade = CascadeType.DETACH)
    // just a reference, the actual fk column is in game not here !
    private List<PlayingCard> playingCards = new ArrayList<>();

    public Player(User user, Game game, Boolean human) {
        LocalDateTime localDateAndTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm-ssSSS-nnnnnnnnn");
        String result = localDateAndTime.format(formatter);
        this.created = result.substring(2, 20);
        this.user = user;
        this.game = game;
        this.human = human;
    }

    public Player(User user, Game game, Boolean human, String avatar, AiLevel aiLevel) {
        this(user, game, human);
        this.avatar = avatar;
        this.aiLevel = aiLevel;
    }

    @Override
    public String toString() {
        return "Player{" +
                "playerId=" + playerId +
                ", created='" + created + '\'' +
                // fk
                ", userId='" + user.getUserId() + '\'' +
                ", gameId='" + game.getGameId() + '\'' + // todo fix
                ", human=" + human +
                ", avatar='" + avatar + '\'' +
                ", aiLevel='" + aiLevel + '\'' +
                '}';
    }
}

