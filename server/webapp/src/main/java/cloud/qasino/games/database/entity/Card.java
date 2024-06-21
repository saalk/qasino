package cloud.qasino.games.database.entity;

import cloud.qasino.games.database.entity.enums.card.Face;
import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.database.entity.enums.card.Position;
import com.fasterxml.jackson.annotation.*;
import com.voodoodyne.jackson.jsog.JSOGGenerator;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.*;
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
@JsonIdentityInfo(generator = JSOGGenerator.class, property = "cardId")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "card", indexes =
        { @Index(name = "cards_game_index", columnList = "game_id", unique = false ),
          // not needed : @Index(name = "cards_index", columnList = "card_id", unique = true )
        })
public class Card {

    // @formatter:off
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_id")
    private long cardId;
    @JsonIgnore
    @Column(name = "created", length = 25)
    private String created;
    @Column(name = "rankSuit", length = 3, nullable = false)
    private String rankSuit;

    // Foreign keys
    @JsonIgnore
    // many [Card] can be part of one [Game]
    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "game_id", referencedColumnName = "game_id", foreignKey =
    @ForeignKey(name =
            "fk_game_id"), nullable=false)
    private Game game;
    @JsonIgnore
    // one [Card] can be part of one [Player] or none
    @OneToOne(optional=true, cascade = CascadeType.DETACH, fetch= FetchType.LAZY)
    @JoinColumn(name = "player_id", referencedColumnName = "player_id", foreignKey =
    @ForeignKey(name =
            "fk_player_id"), nullable=true)
    private Player hand;

    // Normal fields
    @Column(name = "sequence")
    private int sequence;
    @Enumerated(EnumType.STRING)
    @Column(name = "location", nullable = false)
    private Location location;
    @Enumerated(EnumType.STRING)
    @Column(name = "position", nullable = false)
    private Position position;
    @Enumerated(EnumType.STRING)
    @Column(name = "face", nullable = false)
    private Face face;

    // References - actual FK are in other tables
    // none

    public Card() {
        LocalDateTime localDateAndTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm-ssSSS-nnnnnnnnn");
        String result = localDateAndTime.format(formatter);
        this.created = result.substring(0, 20);
    }

    public Card(String rankSuit, Game game, Player player, int sequence, Location location) {
        this();
        this.rankSuit = rankSuit;
        this.game = game;
        this.hand = player;
        this.sequence = sequence;
        this.location = location;
        this.position = Position.SHUFFLED;
        this.face = Face.DOWN;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return cardId == card.cardId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardId);
    }

    @Override
    public String toString () {
        return this.rankSuit;
    }

}
