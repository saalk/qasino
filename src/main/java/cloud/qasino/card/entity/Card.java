package cloud.qasino.card.entity;

import cloud.qasino.card.entity.enums.GameType;
import cloud.qasino.card.entity.enums.Rank;
import cloud.qasino.card.entity.enums.Suit;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.left;
import static org.apache.commons.lang3.StringUtils.right;

/**
 * <H2>Description</H2> A playing card used for playing card gameDtos. A complete set of cards is
 * called a pack (UK English), deck (US English), or set (Universal), and the subset of cards held
 * at one time by a {@link Player player} during a {@link Game game} is commonly called a {@link
 * Hand Hand}. <H2>Relations</H2> Card is associated to {@link Rank enum Rank} and {@link Suit enum
 * Suit}. Because you must have both a Suit and a Rank for a valid Card, they are parameters for the
 * constructor. <p><img src="../../../../../../src/main/resources/plantuml/Card.png" alt="UML">
 *
 * @author Klaas van der Meulen
 * @version 1.0
 * @since v1 - console game
 */

@Entity
@Table(name = "CARD",
		indexes = {
			@Index(name = "CARD_INDEX", columnList = "CARD_ID")},
		uniqueConstraints = {
		@UniqueConstraint(name="UC_RANK_SUIT", columnNames = {"RANK", "SUIT"})
		}		)
@DynamicUpdate
@Getter
@Setter
@Slf4j
//@Relation(value = "card", collectionRelation = "cards")
//@JsonIdentityInfo(generator=JSOGGenerator.class)
// - this annotation adds @Id to prevent chain loop
// - you could also use @JsonManagedReference and @JsonBackReference
public class Card implements Serializable {
	
	// 13 progressing ranks 2 to 10, jack, queen, king, ace.
	//@JsonIgnore
	@Id
	@Column(name = "CARD_ID", length = 3)
	//////@JsonProperty("cardId")
	@Setter(AccessLevel.NONE)
	private String cardId;
	
	@Enumerated(EnumType.STRING)
	//@org.hibernate.annotations.Type(type = "nl.knikit.cardgames.model.enumlabel.LabeledEnumType")
	@Column(name = "RANK", nullable = false, length = 50)
	////@JsonProperty("rank")
	private Rank rank;
	
	@Enumerated(EnumType.STRING)
	//@org.hibernate.annotations.Type(type = "nl.knikit.cardgames.model.enumlabel.LabeledEnumType")
	@Column(name = "SUIT", nullable = false, length = 50)
	////@JsonProperty("suit")
	private Suit suit;
	
	@Column(name = "VALUE")
	////@JsonProperty("value")
	private int value;
	
	public Card() {
	}
	
	public Card(Rank rank, Suit suit) {
		this();
		if (rank == null || suit == null)
			throw new NullPointerException(rank + ", " + suit);
		this.rank = rank;
		this.suit = suit;
		
		final StringBuilder builder = new StringBuilder();
		this.cardId = builder.append(rank.getLabel()).append(suit.getLabel()).toString();
		
		switch (rank) {
			case JOKER:
				value = 0;
				break;
			case ACE:
				value = 1;
				break;
			case KING:
				value = 13;
				break;
			case QUEEN:
				value = 12;
				break;
			case JACK:
				value = 11;
				break;
			default:
				value = Integer.parseInt(rank.getLabel());
		}
		this.value = value != 0 ? rank.getValue(GameType.HIGHLOW) : 0;
	}
	
	public Card(String cardId) {
		this();
		
		if (cardId.isEmpty() || !(cardId.length() > 1)) {
			throw new NullPointerException(cardId + " empty cardId");
		}
		this.cardId = cardId;
		
		// auto fill the rest for convenience
		String rankPart = cardId.length()==2?left(cardId,1):left(cardId,2);
		this.rank = Rank.fromLabel(rankPart);
		this.suit = Suit.fromLabel(right(cardId, 1));
		
		switch (rank) {
			case JOKER:
				value = 0;
				break;
			case ACE:
				value = 1;
				break;
			case KING:
				value = 13;
				break;
			case QUEEN:
				value = 12;
				break;
			case JACK:
				value = 11;
				break;
			default:
				value = Integer.parseInt(rank.getLabel());
		}
		this.value = value != 0 ? rank.getValue(GameType.HIGHLOW) : 0;
	}
	
	public void setCardId(String cardId){
		// auto fill the rest for convenience
		String rankPart = cardId.length()==2?left(cardId,1):left(cardId,2);
		this.rank = Rank.fromLabel(rankPart);
		this.suit = Suit.fromLabel(right(cardId, 1));
		
		this.cardId = cardId;
		
		switch (rank) {
			case JOKER:
				value = 0;
				break;
			case ACE:
				value = 1;
				break;
			case KING:
				value = 13;
				break;
			case QUEEN:
				value = 12;
				break;
			case JACK:
				value = 11;
				break;
			default:
				value = Integer.parseInt(rank.getLabel());
		}
		this.value = value != 0 ? rank.getValue(GameType.HIGHLOW) : 0;
	}
	// static fields and methods to easily make decks and add jokers -> not synchronized so make them final?
	protected static final Card joker = new Card(Rank.JOKER, Suit.JOKERS);
	
	protected static List<Card> prototypeDeck = new ArrayList<>();
	
	static {
		for (Suit suit : Suit.values()) {
			if (suit != Suit.JOKERS) {
				for (Rank rank : Rank.values()) {
					if (rank != Rank.JOKER) {
						prototypeDeck.add(new Card(rank, suit));
					}
				}
			}
		}
	}
	

	/*public void setCardId() {
		final StringBuilder builder = new StringBuilder();
		this.cardId = builder.append(rank.getLabel()).append(suit.getLabel()).toString();
	
	}*/
	
	public static List<Card> newDeck(int addJokers) {
		//List<Card> newDeck = prototypeDeck; // #1 do not do this
		
		List<Card> newDeck = new ArrayList<>();
		newDeck.addAll(prototypeDeck);
		
		String message = String.format("Card newDeck prototypeDeck size: "+ prototypeDeck.size());
		log.info(message);
		
		message = String.format("Card newDeck newDeck size: "+ newDeck.size());
		log.info(message);
		
		for (int i = 0; i < addJokers; i++) {
			newDeck.add(joker); // this adds jokers to #1 prototypedeck that is a class variable !!
			
			message = String.format("Card newDeck add jokers: %s", i + " / "+ addJokers);
			log.info(message);
		}
		return newDeck;
	}
	

	
	//@JsonIgnore
	public static boolean isValidCard(String input) {
		
		try {
			Card check = new Card (input);
		} catch (NullPointerException e) {
			return false;
		}
		return true;
	}
	
	//@JsonIgnore
	public boolean isJoker() {
		String jokerCard = cardId;
		return jokerCard=="RJ";
	}

}
