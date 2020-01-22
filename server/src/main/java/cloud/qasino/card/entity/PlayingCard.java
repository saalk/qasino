package cloud.qasino.card.entity;

import cloud.qasino.card.entity.enums.playingcard.Face;
import cloud.qasino.card.entity.enums.playingcard.Location;
import cloud.qasino.card.entity.enums.playingcard.Position;
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
        property = "playingCardId")
//@JsonIdentityInfo(generator=JSOGGenerator.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "playingcards", indexes =
        { //@Index(name = "playingCards_game_index", columnList = "game_id", unique = false ),
          @Index(name = "playingcards_index", columnList = "playingcard_id", unique = true )})
public class PlayingCard {
    
    @Id
    @GeneratedValue
    @Column(name = "playingcard_id", nullable = false)
    private int playingCardId;

    @JsonIgnore
    @Column(name = "created", length = 25)
    private String created;

    @Column(name = "card", length = 3, nullable = false)
    private String card;

    // Foreign keys

    @JsonIgnore
    // SF: a shuffled PlayingCard is added to a Game at the start
    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "game_id", referencedColumnName = "game_id", foreignKey = @ForeignKey(name =
            "fk_game_id"), nullable=false)
    private Game game;

    @JsonIgnore
    // HO: A Player sometimes holds a PlayingCard after dealing
    @OneToOne(optional=true, cascade = CascadeType.DETACH, fetch= FetchType.LAZY)
    @JoinColumn(name = "player_id", referencedColumnName = "player_id", foreignKey =
    @ForeignKey(name =
            "fk_player_id"), nullable=true)
    private Player hand;


    // Normal fields

    // current sequence of the card in the deck or hand
    @Column(name = "sequence")
    private int sequence;

    // current location for the card (can be hand or not)
    @Enumerated(EnumType.STRING)
    @Column(name = "location", nullable = false)
    private Location location;

    // current Postion for the card in the location
    @Enumerated(EnumType.STRING)
    @Column(name = "position", nullable = false)
    private Position position;

    // current Face for the card in the location (up or down)
    @Enumerated(EnumType.STRING)
    @Column(name = "face", nullable = false)
    private Face face;

    // References

    public PlayingCard() {
        LocalDateTime localDateAndTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm-ssSSS-nnnnnnnnn");
        String result = localDateAndTime.format(formatter);
        this.created = result.substring(2, 20);

    }

    public PlayingCard(String card, Game game, Player player, int sequence, Location location) {
        this();
        this.card = card;
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
        PlayingCard that = (PlayingCard) o;
        return playingCardId == that.playingCardId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(playingCardId);
    }

    @Override
    public String toString() {
        return "PlayingCard{" +
                "playingCardId=" + playingCardId +
                ", created='" + created + '\'' +
                ", cardId=" + card +
                ", game=" + game +
                ", hand=" + hand +
                ", sequence=" + sequence +
                ", location=" + location +
                '}';
    }
}
