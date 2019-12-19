package nl.knikit.card.mapper;

import nl.knikit.card.entity.*;
import nl.knikit.card.vo.*;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Component
public class CardGameMapperUtil {
	
	public CardGamePlayer convertToCardGame(Player player) {
		ModelMapper modelMapper = new ModelMapper();
		CardGamePlayer cardGamePlayer = modelMapper.map(player, CardGamePlayer.class);
		
		List<CardGame> CardGames = new ArrayList<>();
		if (player.getGames() != null) {
			for (Game game : player.getGames()) {
				// CardGames.add(convertToCardGame(game)); this created a loop
				modelMapper = new ModelMapper();
				CardGame CardGame = modelMapper.map(game, CardGame.class);
				modelMapper.addMappings(new GameMapFromEntity()); // customer mapping
				CardGame.setName();
				CardGames.add(CardGame);
			}
			cardGamePlayer.setCardGames(CardGames);
		} else {
			cardGamePlayer.setCardGames(null);
		}
		
		List<CardGameCasino> CardGameCasinos = new ArrayList<>();
		if (player.getCasinos() != null) {
			for (Casino casino : player.getCasinos()) {
				// CardGameCasinos.add(convertToCardGame(casino)); this created a loop
				modelMapper = new ModelMapper();
				CardGameCasino CardGameCasino = modelMapper.map(casino, CardGameCasino.class);
				//modelMapper.addMappings(new CasinoMapFromEntity()); // customer mapping
				CardGameCasinos.add(CardGameCasino);
			}
			cardGamePlayer.setCardGameCasinos(CardGameCasinos);
		} else {
			cardGamePlayer.setCardGameCasinos(null);
		}
		
		cardGamePlayer.setName();
		cardGamePlayer.setWinCount();
		
		return cardGamePlayer;
	}
	
	public Player convertToEntity(CardGamePlayer cardGamePlayer) throws ParseException {
		ModelMapper modelMapper = new ModelMapper();
		Player player = modelMapper.map(cardGamePlayer, Player.class);
		
		player.setGames(null);
		player.setCasinos(null);
		
		return player;
	}
	
	public CardGame convertToCardGame(Game game) {
		ModelMapper modelMapper = new ModelMapper();
		CardGame CardGame = modelMapper.map(game, CardGame.class);
		modelMapper.addMappings(new GameMapFromEntity()); // customer mapping
		
		CardGame.setName();
		if (game.getPlayer() != null) {
			// CardGame.setWinner(convertToCardGame(game.getPlayer())); // this created a loop...
			modelMapper = new ModelMapper();
			CardGamePlayer CardGamePlayer = modelMapper.map(game.getPlayer(), CardGamePlayer.class);
			CardGamePlayer.setCardGames(null);
			CardGamePlayer.setName();
			CardGamePlayer.setWinCount();
			CardGame.setWinner(CardGamePlayer);
		} else {
			CardGame.setWinner(null);
		}
		CardGame.setCardsDealt();
		CardGame.setCardsLeft();
		return CardGame;
	}
	
	public CardGame convertFromGameEntity(Game game) throws Exception {
		ModelMapper modelMapper = new ModelMapper();
		CardGame cardGame = modelMapper.map(game, CardGame.class);
		
		cardGame.setName();
		if (game.getPlayer() != null) {
			// CardGame.setWinner(convertToCardGame(game.getPlayer())); // this created a loop...
			modelMapper = new ModelMapper();
			CardGamePlayer CardGamePlayer = modelMapper.map(game.getPlayer(), CardGamePlayer.class);
			CardGamePlayer.setCardGames(null);
			CardGamePlayer.setName();
			CardGamePlayer.setWinCount();
			cardGame.setWinner(CardGamePlayer);
		} else {
			cardGame.setWinner(null);
		}
		
		// fill casinos and hands in casinos correct
		List<CardGameCasino> CardGameCasinos = new ArrayList<>();
		List<CardGameHand> CardGameHands = new ArrayList<>();
		
		// are casinos present
		if (game.getCasinos() != null) {
			for (Casino casino : game.getCasinos()) {
				CardGameHands.clear();
				modelMapper = new ModelMapper();
				CardGameCasino CardGameCasino = modelMapper.map(casino, CardGameCasino.class);
				//modelMapper.addMappings(new CasinoMapFromEntity()); // customer mapping
				
				if (casino.getGame() != null) {
					modelMapper = new ModelMapper();
					CardGame CardGame = modelMapper.map(casino.getGame(), CardGame.class);
					
					CardGame.setCards(null);
					CardGame.setWinner(null);
					
					CardGameCasino.setCardGame(CardGame);
					CardGameCasino.setBalance();
					
				}
				
				if (casino.getPlayer() != null) {
					modelMapper = new ModelMapper();
					CardGamePlayer CardGamePlayer = modelMapper.map(casino.getPlayer(), CardGamePlayer.class);
					
					CardGamePlayer.setCardGames(null);
					CardGamePlayer.setName();
					CardGamePlayer.setWinCount();
					
					CardGameCasino.setCardGamePlayer(CardGamePlayer);
					CardGameCasino.setName();
					CardGameCasino.setBalance();
				}
				
				if (casino.getHands() != null) {
					for (Hand hand : casino.getHands()) {
						
						modelMapper = new ModelMapper();
						CardGameHand CardGameHand = modelMapper.map(hand, CardGameHand.class);
						
						if (hand.getCard() != null) {
							modelMapper = new ModelMapper();
							CardGameCard CardGameCard = modelMapper.map(hand.getCard(), CardGameCard.class);
							
							CardGameHand.setCardGameCard(CardGameCard);
						}
						
						CardGameHand.setCardGamePlayer(null);
						CardGameHand.setCardGameCasino(null);
						CardGameHand.setName();
						CardGameHands.add(CardGameHand);
					}
					CardGameCasino.setCardGameHands(CardGameHands);
				}
				CardGameCasinos.add(CardGameCasino);
			}
			cardGame.setPlayers(CardGameCasinos);
		} else {
			cardGame.setPlayers(null);
		}
		
		// fill decks
		List<CardGameDeck> cardGameDecks = new ArrayList<>();
		if (game.getDecks() != null) {
			for (Deck deck : game.getDecks()) {
				
				modelMapper = new ModelMapper();
				CardGameDeck cardGameDeck = modelMapper.map(deck, CardGameDeck.class);
				//modelMapper.addMappings(new CasinoMapFromEntity()); // customer mapping
				
				// fill card in deck
				if (deck.getCard() != null) {
					cardGameDeck.setCardGameCard(convertToCardGame(deck.getCard()));
					cardGameDeck.setName();
				} else {
					cardGameDeck.setCardGameCard(null);
				}
				
				// fill player in deck
				if (deck.getDealtTo() != null) {
					
					cardGameDeck.setDealtToCardGameCasino(convertToCardGame(deck.getDealtTo()));
				} else {
					cardGameDeck.setDealtToCardGameCasino(null);
				}
				cardGameDecks.add(cardGameDeck);
			}
			cardGame.setCards(cardGameDecks);
		} else {
			cardGame.setCards(null);
			
		}
		cardGame.setCardsDealt();
		cardGame.setCardsLeft();
		return cardGame;
	}
	
	public Game convertToEntity(CardGame cardGame) throws ParseException {
		ModelMapper modelMapper = new ModelMapper();
		Game game = modelMapper.map(cardGame, Game.class);
		if (cardGame.getWinner() != null) {
			
			// game.setPlayer(convertToEntity(CardGame.getWinner())); // this creates a loop ..
			modelMapper = new ModelMapper();
			game.setPlayer(modelMapper.map(cardGame.getWinner(), Player.class));
		} else {
			game.setPlayer(null);
		}
		return game;
	}
	
	public CardGameDeck convertToCardGame(Deck deck) throws Exception {
		ModelMapper modelMapper = new ModelMapper();
		CardGameDeck CardGameDeck = modelMapper.map(deck, CardGameDeck.class);
		
		if (deck.getDealtTo() != null) {
			//CardGameDeck.setDealtToCardGame(convertToCardGame(deck.getDealtTo())); // does this create a loop ?
			CardGameDeck.setDealtToCardGameCasino(modelMapper.map(deck.getDealtTo(), CardGameCasino.class));
		} else {
			CardGameDeck.setDealtToCardGameCasino(null);
		}
		if (deck.getGame() != null) {
			CardGameDeck.setCardGame(convertToCardGame(deck.getGame()));
		} else {
			CardGameDeck.setCardGame(null);
		}
		if (deck.getCard() != null) {
			CardGameDeck.setCardGameCard(convertToCardGame(deck.getCard()));
		} else {
			CardGameDeck.setCardGameCard(null);
		}
		
		CardGameDeck.setName();
		return CardGameDeck;
	}
	
	public Deck convertToEntity(CardGameDeck CardGameDeck) throws ParseException {
		ModelMapper modelMapper = new ModelMapper();
		Deck deck = modelMapper.map(CardGameDeck, Deck.class);
		
		if (CardGameDeck.getDealtToCardGameCasino() != null) {
			deck.setDealtTo(convertToEntity(CardGameDeck.getDealtToCardGameCasino())); // this creates a loop ..
		} else {
			deck.setDealtTo(null);
		}
		if (CardGameDeck.getCardGame() != null) {
			deck.setGame(convertToEntity(CardGameDeck.getCardGame())); // this creates a loop ..
		} else {
			deck.setGame(null);
		}
		if (CardGameDeck.getCardGameCard() != null) {
			deck.setCard(convertToEntity(CardGameDeck.getCardGameCard())); // this creates a loop ..
		} else {
			deck.setCard(null);
		}
		
		deck.setDealtTo(convertToEntity(CardGameDeck.getDealtToCardGameCasino())); // this creates a loop ..
		deck.setGame(convertToEntity(CardGameDeck.getCardGame())); // this creates a loop ..
		deck.setCard(convertToEntity(CardGameDeck.getCardGameCard())); // this creates a loop ..
		
		return deck;
	}
	
	public CardGameCasino convertToCardGame(Casino casino) throws Exception {
		ModelMapper modelMapper = new ModelMapper();
		CardGameCasino cardGameCasino = modelMapper.map(casino, CardGameCasino.class);
		
		if (casino.getPlayer() != null) {
			if (casino.getPlayer() != null) {
				// CardGameCasino.setWinner(convertToCardGame(casino.getPlayer())); // this created a loop...
				modelMapper = new ModelMapper();
				CardGamePlayer cardGamePlayer = modelMapper.map(casino.getPlayer(), CardGamePlayer.class);
				cardGamePlayer.setCardGameCasinos(null);
				cardGamePlayer.setName();
				cardGamePlayer.setWinCount();
				cardGameCasino.setCardGamePlayer(cardGamePlayer);
			} else {
				cardGameCasino.setCardGamePlayer(null);
			}
			
		} else {
			cardGameCasino.setCardGamePlayer(null);
		}
		if (casino.getGame() != null) {
			cardGameCasino.setCardGame(convertToCardGame(casino.getGame()));
		} else {
			cardGameCasino.setCardGame(null);
		}
		
		cardGameCasino.setName();
		cardGameCasino.setBalance();
		return cardGameCasino;
	}
	
	public Casino convertToEntity(CardGameCasino cardGameCasino) throws ParseException {
		ModelMapper modelMapper = new ModelMapper();
		Casino casino = modelMapper.map(cardGameCasino, Casino.class);
		
		if (cardGameCasino.getCardGamePlayer() != null) {
			casino.setPlayer(convertToEntity(cardGameCasino.getCardGamePlayer())); // this creates a loop ..
		} else {
			casino.setPlayer(null);
		}
		if (cardGameCasino.getCardGame() != null) {
			casino.setGame(convertToEntity(cardGameCasino.getCardGame())); // this creates a loop ..
		} else {
			casino.setGame(null);
		}
		
		casino.setPlayer(convertToEntity(cardGameCasino.getCardGamePlayer())); // this creates a loop ..
		casino.setGame(convertToEntity(cardGameCasino.getCardGame())); // this creates a loop ..
		
		return casino;
	}
	
	public CardGameHand convertToCardGame(Hand hand) throws Exception {
		ModelMapper modelMapper = new ModelMapper();
		CardGameHand CardGameHand = modelMapper.map(hand, CardGameHand.class);
		
		if (hand.getPlayer() != null) {
			CardGameHand.setCardGamePlayer(convertToCardGame(hand.getPlayer())); // does this create a loop ?
			//CardGameHand.setCardGamePlayer(modelMapper.map(hand.getPlayer(), CardGamePlayer.class));
		} else {
			CardGameHand.setCardGamePlayer(null);
		}
		if (hand.getCasino() != null) {
			CardGameHand.setCardGameCasino(convertToCardGame(hand.getCasino()));
		} else {
			CardGameHand.setCardGameCasino(null);
		}
		if (hand.getCard() != null) {
			CardGameHand.setCardGameCard(convertToCardGame(hand.getCard()));
		} else {
			CardGameHand.setCardGameCard(null);
		}
		
		CardGameHand.setName();
		return CardGameHand;
	}
	
	public Hand convertToEntity(CardGameHand cardGameHand) throws ParseException {
		ModelMapper modelMapper = new ModelMapper();
		Hand hand = modelMapper.map(cardGameHand, Hand.class);
		
		if (cardGameHand.getCardGamePlayer() != null) {
			hand.setPlayer(convertToEntity(cardGameHand.getCardGamePlayer())); // this creates a loop ..
		} else {
			hand.setPlayer(null);
		}
		if (cardGameHand.getCardGameCasino() != null) {
			hand.setCasino(convertToEntity(cardGameHand.getCardGameCasino())); // this creates a loop ..
		} else {
			hand.setCasino(null);
		}
		if (cardGameHand.getCardGameCard() != null) {
			hand.setCard(convertToEntity(cardGameHand.getCardGameCard())); // this creates a loop ..
		} else {
			hand.setCard(null);
		}
		
		hand.setPlayer(convertToEntity(cardGameHand.getCardGamePlayer())); // this creates a loop ..
		hand.setCasino(convertToEntity(cardGameHand.getCardGameCasino())); // this creates a loop ..
		hand.setCard(convertToEntity(cardGameHand.getCardGameCard())); // this creates a loop ..
		
		return hand;
	}
	
	public CardGameCard convertToCardGame(Card card) throws Exception {
		ModelMapper modelMapper = new ModelMapper();
		CardGameCard CardGameCard = modelMapper.map(card, CardGameCard.class);
		CardGameCard.setSuit(card.getSuit());
		CardGameCard.setRank(card.getRank());
		CardGameCard.setValue(card.getValue());
		return CardGameCard;
	}
	
	public Card convertToEntity(CardGameCard cardGameCard) throws ParseException {
		ModelMapper modelMapper = new ModelMapper();
		Card card = modelMapper.map(cardGameCard, Card.class);
		return card;
	}
	
}
