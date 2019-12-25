package cloud.qasino.card.entity;

import cloud.qasino.card.entity.enums.AiLevel;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Data
@Table(name = "players", indexes = {@Index(name = "players_index", columnList = "playerId")})
public class Player extends User {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private int playerId;

    @Column(length = 25)
    private String created;

    // Cascade = any change happened on this entity must cascade to the parent/child as well
    // since this is the child Deck: do nothing when Deck is delete on the parent Game
    // meaning do not set cascade options
    @ManyToOne(optional=false, cascade = CascadeType.DETACH, fetch= FetchType.LAZY)
    @JoinColumn(name = "gameId", referencedColumnName = "gameId", foreignKey = @ForeignKey(name =
            "gameId"), nullable=false)
    private User user;

    //@Enumerated(EnumType.STRING)
    @Column(nullable = true, length = 50)
    private String avatar;

    @Setter(AccessLevel.NONE)
    private boolean human;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true, length = 50)
    private AiLevel aiLevel;

    public Player(User user, Boolean human) {
        LocalDateTime localDateAndTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm-ssSSS-nnnnnnnnn");
        String result = localDateAndTime.format(formatter);
        this.created = result.substring(2, 20);
        this.user = user;
        this.human = human;
    }

    public Player(User user, Boolean human, String avatar, AiLevel aiLevel) {
        this(user, human);
        this.avatar = avatar;
        this.aiLevel = aiLevel;
    }

    @Override
    public String toString() {
        return "Player{" +
                "playerId=" + playerId +
                ", created='" + created + '\'' +
                ", human=" + human +
                ", avatar='" + avatar + '\'' +
                ", aiLevel='" + avatar + '\'' +
                ", userId='" + super.getUserId() +
                '}';
    }
}

