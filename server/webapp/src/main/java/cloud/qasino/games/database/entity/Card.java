package cloud.qasino.games.database.entity;

import cloud.qasino.games.database.entity.enums.card.Face;
import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.database.entity.enums.card.Position;
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
        property = "cardId")
//@JsonIdentityInfo(generator=JSOGGenerator.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "card", indexes =
        { //@Index(name = "cards_game_index", columnList = "game_id", unique = false ),
          // not needed : @Index(name = "cards_index", columnList = "card_id", unique = true )
        })
public class Card {
    
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
    // SF: a shuffled Card is added to a GameSubTotals at the start
    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "game_id", referencedColumnName = "game_id", foreignKey =
    @ForeignKey(name =
            "fk_game_id"), nullable=false)
    private Game game;

    @JsonIgnore
    // HO: A Player sometimes holds a Card after dealing
    @OneToOne(optional=true, cascade = CascadeType.DETACH, fetch= FetchType.LAZY)
    @JoinColumn(name = "player_id", referencedColumnName = "player_id", foreignKey =
    @ForeignKey(name =
            "fk_player_id"), nullable=true)
    private Player hand;

    // Normal fields

    // current sequence of the \"card\" in the deck or hand
    @Column(name = "sequence")
    private int sequence;

    // current location for the \"card\" (can be hand or not)
    @Enumerated(EnumType.STRING)
    @Column(name = "location", nullable = false)
    private Location location;

    // current Postion for the \"card\" in the location TODO make Ordered
    @Enumerated(EnumType.STRING)
    @Column(name = "position", nullable = false)
    private Position position;

    // current Face for the \"card\" in the location (up or down)
    @Enumerated(EnumType.STRING)
    @Column(name = "face", nullable = false)
    private Face face;

    // References

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
        Card that = (Card) o;
        return cardId == that.cardId;
    }

    @Override
    public String toString () {
        return this.rankSuit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardId);
    }

}
