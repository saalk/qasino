package cloud.qasino.card.entity;

import cloud.qasino.card.entity.enums.CardLocation;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@NoArgsConstructor
@Table(name = "deck",
        indexes = { @Index(name = "deckCardId_index", columnList = "cardId"),
                    @Index(name = "deckGameId_index", columnList = "gameId")})
@Data
public class Deck extends Game {
    
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "deckId")
    private int deckId;
    
    @Column(name = "created", length = 25)
    private String created;
    
    // Cascade = any change happened on this entity must cascade to the parent/child as well
    // since this is the child Deck: do nothing when Deck is delete on the parent Game
    // meaning do not set cascade options
    @ManyToOne(optional=false, cascade = CascadeType.DETACH, fetch= FetchType.LAZY)
    @JoinColumn(name = "gameId", referencedColumnName = "gameId", foreignKey = @ForeignKey(name =
            "gameId"), nullable=false)
    private Game game;

    @OneToOne(optional=true, cascade = CascadeType.DETACH)
    // TODO No fk since that created a unique foreign key that only allows unique cards in all decks..?
    @JoinColumn(name = "cardId", referencedColumnName = "cardId",foreignKey = @ForeignKey(name =
            "cardId"), nullable=true)
    private Card card;

    private int cardOrder;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CardLocation cardLocation;

    // Cascade = any change happened on this entity must cascade to the parent/child as well
    // since this is the child Deck: do nothing when Deck is delete on the parent Game
    // meaning do not set cascade options
    @ManyToOne(optional=true, cascade = CascadeType.DETACH, fetch= FetchType.LAZY)
    @JoinColumn(name = "playerId", referencedColumnName = "playerId", foreignKey =
    @ForeignKey(name =
            "playerId"), nullable=true)
    private Player dealtTo;

    /**
     * Hibernate, and code in general that creates objects via reflection use newInstance()
     * to create a new instance of your classes. This method requires a public or private
     * no-arg constructor to be able to instantiate the object.
     */

    public Deck(Game game) {
        LocalDateTime localDateAndTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm-ssSSS-nnnnnnnnn");
        String result = localDateAndTime.format(formatter);
        this.created = result.substring(2, 20);
        this.game = game;
    }

    @Override
    public String toString() {
        return "Deck{" +
                "deckId=" + deckId +
                ", created='" + created + '\'' +
                ", gameId=" + game.getGameId() +
                ", cardId=" + card.getCardId() +
                ", cardOrder=" + cardOrder +
                ", cardLocation=" + cardLocation.getLabel() +
                ", dealtTo=" + dealtTo.getPlayerId() +
                '}';
    }
}
