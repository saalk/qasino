package nl.knikit.card.entity;

import lombok.Getter;
import lombok.Setter;
import nl.knikit.card.entity.enums.CardAction;
import nl.knikit.card.entity.enums.CardLocation;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * <H1>Hand</H1> A players Hand that can hold one or more cards. <p>For Hand to reuses the Card
 * java-code we could implement this in 2 ways: class / interface inheritance or object composition
 * <p><h2> Hand IS-A list of Cards</h2>
 * Codify this via Hand extends Card. This is <u>Class Inheritance</u> via static (compile-time) binding.
 * When Hand implements Card this is called <u>Interface Inheritance</u>.
 * So Hand could extend or implement Card meaning a player-child relationship having subclasses.
 * Since Hand is NOT a specific type of Cards we could better use a 'HAS-A' relationship.
 * <h2> Hand HAS-A list of Cards</h2>
 * Codify this via Card handCards = new Card(). This is <u>Object Composition</u> via dynamic (run-time)
 * binding. So if only you want to reuse code and there is no IS-A relationship in sight, use
 * composition. When the association is loose composition is better known as aggregation.
 *
 * @author Klaas van der Meulen
 * @version 1.0
 * @since v1 - console game
 */
@Entity
@DynamicUpdate
@Table(name = "HAND",
        indexes = {
            @Index(columnList = "PLAYER_ID", name = "PLAYER_ID_INDEX"),
            @Index(columnList = "CARD_ID", name = "CARD_ID_INDEX"),
            @Index(columnList = "CASINO_ID", name = "CASINO_ID_INDEX")})
//@Relation(value = "Hand", collectionRelation = "hands")
//@JsonIdentityInfo(generator=JSOGGenerator.class)
// - this annotation adds @Id to prevent chain loop
// - you could also use @JsonManagedReference and @JsonBackReference
@Getter
@Setter
public class Hand implements Serializable {
    
    // 6 fields
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "HAND_ID")
    ////@JsonProperty("handId")
    private int handId;
    
    @Column(name = "CREATED", length = 25)
    private String created;
    
    // Cascade = any change happened on this entity must cascade to the parent/child as well
    // since this is the child Hand: do nothing when Hand is delete on the parent Player
    // meaning do not set cascade options
    //@JsonIgnore
    @ManyToOne(optional= false, cascade = CascadeType.DETACH, fetch= FetchType.LAZY)
    @JoinColumn(name = "PLAYER_ID", referencedColumnName = "PLAYER_ID", foreignKey = @ForeignKey(name = "PLAYER_ID"), nullable=false)
    ////@JsonProperty("player")
    private Player player;
    
    // Cascade = any change happened on this entity must cascade to the parent/child as well
    // since this is the child Hand: do nothing when Hand is delete on the parent Casino
    // meaning do not set cascade options
    //@JsonIgnore
    @ManyToOne(optional = false, cascade = CascadeType.DETACH, fetch= FetchType.LAZY)
    @JoinColumn(name = "CASINO_ID", referencedColumnName = "CASINO_ID", foreignKey = @ForeignKey(name = "CASINO_ID"))
    ////@JsonProperty("casino")
    private Casino casino;
    
    //@JsonIgnore
    @OneToOne(optional=true)
    @JoinColumn(name = "CARD_ID", referencedColumnName = "CARD_ID")
    ////@JsonProperty("card")
    private Card card;
    
    @Column(name = "CARD_ORDER")
    ////@JsonProperty("cardOrder")
    private int cardOrder;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "CARD_LOCATION", nullable = false)
    ////@JsonProperty("cardLocation")
    private CardLocation cardLocation;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "CARD_ACTION", nullable = false)
    ////@JsonProperty("cardAction")
    private CardAction cardAction;
    
	@Column(name = "ROUND")
	private int round;
	
	@Column(name = "TURN")
	private int turn;
	
    public Hand(){
        LocalDateTime localDateAndTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm-ssSSS-nnnnnnnnn");
        String result = localDateAndTime.format(formatter);
        this.created = result.substring(2, 20);
    }

}
