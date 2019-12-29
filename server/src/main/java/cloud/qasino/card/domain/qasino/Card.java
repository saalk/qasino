package cloud.qasino.card.domain.qasino;

import cloud.qasino.card.entity.enums.Type;
import cloud.qasino.card.entity.enums.Rank;
import cloud.qasino.card.entity.enums.Suit;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.left;
import static org.apache.commons.lang3.StringUtils.right;

@Data
@Slf4j
public class Card {
	
	// 13 progressing ranks 2 to 10, jack, queen, king, ace.
	private String cardId;
	private Rank rank;
	private Suit suit;

	private int value;
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
		this.value = value != 0 ? rank.getValue(Type.HIGHLOW) : 0;
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
		this.value = value != 0 ? rank.getValue(Type.HIGHLOW) : 0;
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
		this.value = value != 0 ? rank.getValue(Type.HIGHLOW) : 0;
	}
	// static fields and methods to easily make playingCards and add jokers -> not synchronized so make them final?
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
		//List<Card> newDeck = prototypePlayingCard; // #1 do not do this
		
		List<Card> newDeck = new ArrayList<>();
		newDeck.addAll(prototypeDeck);
		
		String message = String.format("prototypeDeck initial size: "+ prototypeDeck.size());
		log.info(message);
		
		message = String.format(" newDeck size: "+ newDeck.size());
		log.info(message);
		
		for (int i = 0; i < addJokers; i++) {
			newDeck.add(joker); // this adds jokers to #1 prototypeplayingCard that is a class variable !!
			
			message = String.format("newDeck add jokers: %s", i + " / "+ addJokers);
			log.info(message);
		}
		return newDeck;
	}

	public static boolean isValidCard(String input) {
		
		try {
			Card check = new Card (input);
		} catch (NullPointerException e) {
			return false;
		}
		return true;
	}
	
	public boolean isJoker() {
		String jokerCard = cardId;
		return jokerCard.equals("RJ");
	}

	private void setValue(Type type) {

		Type localType = type==null ? Type.HIGHLOW : type;

		switch (rank) {
			case JOKER:
				this.value = localType.equals(Type.HIGHLOW) ? 0 : 0;
				this.value = localType.equals(Type.BLACKJACK) ? 0 : 0;
				break;
			case ACE:
				// todo set value Ace for Type
				this.value = 1;
				break;
			case KING:
				this.value = 13;
				break;
			case QUEEN:
				this.value = 12;
				break;
			case JACK:
				this.value = 11;
				break;
			default:
				// 2 until 10
				this.value = Integer.parseInt(rank.getLabel());
		}
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
