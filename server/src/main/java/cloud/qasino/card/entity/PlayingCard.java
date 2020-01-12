package cloud.qasino.card.entity;

import cloud.qasino.card.entity.enums.Move;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Data
@DynamicUpdate
@Table(name = "playingcards", indexes =
        { //@Index(name = "playingCards_game_index", columnList = "game_id", unique = false ),
          @Index(name = "playingcards_index", columnList = "playingcard_id", unique = true )})
public class PlayingCard {
    
    @Id
    @GeneratedValue
    @Column(name = "playingcard_id", nullable = false)
    private int playingCardId;
    
    @Column(name = "created", length = 25)
    private String created;


    // Foreign keys

    // SF: a shuffled PlayingCard is added to a Game at the start
    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "game_id", referencedColumnName = "game_id", foreignKey = @ForeignKey(name =
            "fk_game_id"), nullable=false)
    private Game game;

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

    // current move for the card
    @Enumerated(EnumType.STRING)
    @Column(name = "move", nullable = false)
    private Move move;


    // References

    public PlayingCard() {
        LocalDateTime localDateAndTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm-ssSSS-nnnnnnnnn");
        String result = localDateAndTime.format(formatter);
        this.created = result.substring(2, 20);

    }

    public PlayingCard(Game game, int sequence) {
        this();
        this.game = game;
        this.sequence = sequence;
    }

    @Override
    public String toString() {
        return "PlayingCard{" +
                "playingCardId=" + playingCardId +
                ", created='" + created + '\'' +
                // fk
                ", gameId=" + game.getGameId() +
                ", hand=" + hand.getPlayerId() +
                // fields
                ", sequence=" + sequence +
                ", move=" + move.getLabel() +
                '}';
    }
}
