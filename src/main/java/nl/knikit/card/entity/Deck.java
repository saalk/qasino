package nl.knikit.card.entity;

import lombok.Getter;
import lombok.Setter;
import nl.knikit.card.entity.enums.CardLocation;
import nl.knikit.card.entity.enums.Suit;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * <H2>Description</H2>A "standard" deck of playing cards consists of 52 {@link Card Cards}; 13 in each of
 * the 4 Enum {@link Suit suits} of the following list <ul> <li>{@link Suit Spades} <li>{@link Suit
 * Hearts} <li>{@link Suit Diamonds} <li>{@link Suit Clubs} </ul> <p>The ranks have symbols, so
 * called face cards: ace, king, queen, and jack or numbers ranging from 2 to 10. Optionally one or
 * more jokers can be added while generating a deck.
 * <H2>Relations</H2>
 * Deck 'HAS-A' relationship with a preset list of Cards. It is codified via <b>Composition</b> because
 * you must have Card(s) in a Deck and dealing does not remove Cards (like in real world) so 'new()' is
 * placed in the constructor or a setter.<p> {@code ArrayList<Card>
 * cards;}<br>{@code Deck() {cards = new ArrayList<>()}; loop to add Card to list of cards;}
 * <p><img src="../../../../../../src/main/resources/plantuml/Deck.png" alt="UML">
 * <p>
 * <H3>NB -&gt; Relationships on instance-level</H3>
 * Relations between instances of Classes have the following UML notations<ol>
 * <li><b>Dependency (often confused as Association)</b> - a uni directional reference between a dependent and independent elements.<br>
 * class A -USES- class B: represented by a dashed arrow starting from the dependent class to its dependency.<br>
 * codified: as a parameter via a method or as a local instance reference with new in a method. You only need to import B in A.<br>
 * example: PlayerOld - Die via the PlayerOld.takeTurn(Die die) method that has die.Roll() reference to the Roll method of die.
 * <li><b>Association</b> - a uni or bi directional reference between two classes.<br>
 * class A -NOUN- class B: represented by a line, arrows indicate navigation, has multiplicity at one (uni) or both (bi) ends.<br>
 * codified: define an instance of the associated class and passed in it's setter or constructor.<br>
 * example: Passenger -books- Flight via the class Passenger;Flight flight;setFlight(Flight bookedFlight){} setter.
 * Also Student -enrolls- Course.
 * <li><b>Aggregation</b> - a one-to-many association with separate lifecycles.<br>
 * class A -HAS-A- class B: represented by a line with a hollow diamond at the containing class and 1..* at the collection side.<br>
 * codified: like association but with a list of instances of the associated class.<br>
 * The referenced Object is created (new) outside referring class and then passed as a parameter via setter or method.<br>
 * example: Professor - Class: class Professor;List&gt;Class&lt; classes;addClass(Class newClass){} method.
 * <li><b>Composition</b> - a strong lifecycle dependent association.<br>
 * class A -OWNS- class B: represented by a line with a solid diamond at the containing class and 1..1/2/n at the collection side.<br>
 * codified: like association but via a private final instance variables reference and constructed (new()) in the class constructor.
 * The encapsulated / composed Object (part) is destroyed when the referring / composite Object (whole) is destroyed.
 * Other classes cannot see or use the composed Object.<br>
 * example: Car - Engine: class Car;Engine engine = new Engine;Car(Engine newEngine){}; constructor.
 * </li></ol>
 * <H3>NB -&gt; Relationships on class-level</H3><ol>
 * <li><b>Generalization (also called Specialization)</b> - indicates that one of the two related classes is specialized form or the other.<br>
 * class A IS-A-(SUB)TYPE-OF class B: represented by a line with a hollow triangle shape on the superclass.<br>
 * codified: on class level with A Extends B. Known as inheritance, the superclass (base class) is usually abstract.<br>
 * example: Professor|Student extends Person {}.
 * <li><b>Realization</b> - indicates a contract between a subsystem or framework and its consumer.<br>
 * class A IS-A-KIND-OF class B: represented by a dotted or dashed line and a hollow triangle shape on the interface class.
 * codified: on class level with interface A {};class B implements A {};
 * example: A implements Clonable, Comparable {}.
 * </li></ol>
 *
 * @author Klaas van der Meulen
 * @version 1.0
 * @since v1 - console game
 */
@Entity
@Table(name = "DECK",
        indexes = {
                          @Index(columnList = "CASINO_ID", name = "CASINO_ID_INDEX"),
                          @Index(columnList = "CARD_ID", name = "CARD_ID_INDEX"),
                          @Index(columnList = "GAME_ID", name = "GAME_ID_INDEX")})
        
//        indexes = {
//        @Index(columnList = "GAME_ID", name = "GAME_ID_INDEX")})
//@Relation(value = "deck", collectionRelation = "decks")
@Getter
@Setter
@DynamicUpdate
//@JsonIdentityInfo(generator=JSOGGenerator.class)
// - this annotation adds @Id to prevent chain loop
// - you could also use @JsonManagedReference and @JsonBackReference
public class Deck implements Serializable {
    
    // 5 fields
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "DECK_ID")
    ////@JsonProperty("deckId")
    private int deckId;
    
    @Column(name = "CREATED", length = 25)
    private String created;
    
    // Cascade = any change happened on this entity must cascade to the parent/child as well
    // since this is the child Deck: do nothing when Deck is delete on the parent Game
    // meaning do not set cascade options
    //@JsonIgnore
    @ManyToOne(optional=false, cascade = CascadeType.DETACH, fetch= FetchType.LAZY)
    @JoinColumn(name = "GAME_ID", referencedColumnName = "GAME_ID", foreignKey = @ForeignKey(name = "GAME_ID"), nullable=false)
    //@JsonProperty("game")
    private Game game;
    
    //@JsonIgnore
    @OneToOne(optional=true, cascade = CascadeType.DETACH)
    // TODO No fk since that created a unique foreign key that only allows unique cards in all decks..?
    @JoinColumn(name = "CARD_ID", referencedColumnName = "CARD_ID",foreignKey = @ForeignKey(name = "CARD_ID"), nullable=true)
    ////@JsonProperty("card")
    private Card card;

    @Column(name = "CARD_ORDER")
    ////@JsonProperty("cardOrder")
    private int cardOrder;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "CARD_LOCATION", nullable = false)
    ////@JsonProperty("cardLocation")
    private CardLocation cardLocation;
    
    //@JsonIgnore
    @OneToOne(optional=true)
    @JoinColumn(name = "CASINO_ID", referencedColumnName = "CASINO_ID", foreignKey = @ForeignKey(name = "CASINO_ID"), nullable=true)
    ////@JsonProperty("dealtTo")
    private Casino dealtTo;

    /**
     * Hibernate, and code in general that creates objects via reflection use newInstance()
     * to create a new instance of your classes. This method requires a public or private
     * no-arg constructor to be able to instantiate the object.
     */
    public Deck() {
        LocalDateTime localDateAndTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm-ssSSS-nnnnnnnnn");
        String result = localDateAndTime.format(formatter);
        this.created = result.substring(2, 20);
    }
}
