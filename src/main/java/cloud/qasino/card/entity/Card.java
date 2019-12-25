package cloud.qasino.card.entity;

import cloud.qasino.card.entity.enums.GameType;
import cloud.qasino.card.entity.enums.Rank;
import cloud.qasino.card.entity.enums.Suit;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.left;
import static org.apache.commons.lang3.StringUtils.right;

@Entity
@Table(name = "cards",
		indexes = {
			@Index(name = "cards_index", columnList = "cardId")},
		uniqueConstraints = {
		@UniqueConstraint(name="uc_rank_list", columnNames = {"rank", "suit"})
		}		)
@Data
@Slf4j
public class Card {
	
	// 13 progressing ranks 2 to 10, jack, queen, king, ace.
	@Id
	@Column(length = 3)
	@Setter(AccessLevel.NONE)
	private String cardId;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 50)
	private Rank rank;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 50)
	private Suit suit;
	
	private int value;

	@Column(length = 30)
	private String thumbnailPath;

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

		// todo: set thumbnailPath

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

	@Override
	public String toString() {
		return "Card{" +
				"cardId=" + cardId +
				", rank='" + rank.name() + '\'' +
				", suit='" + suit.name() + '\'' +
				", value=" + value +
				", thumbnailPath='" + thumbnailPath + '\'' +
				'}';
	}
}
