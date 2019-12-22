package cloud.qasino.card.mapper;

import cloud.qasino.card.dto.*;
import cloud.qasino.card.entity.*;
import nl.knikit.card.dto.*;
import nl.knikit.card.entity.*;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Component
public class ModelMapperUtil {
	
	public PlayerDto convertToDto(Player player) {
		ModelMapper modelMapper = new ModelMapper();
		PlayerDto playerDto = modelMapper.map(player, PlayerDto.class);
		
		List<GameDto> gameDtos = new ArrayList<>();
		if (player.getGames() != null) {
			for (Game game: player.getGames()) {
				// gameDtos.add(convertToDto(game)); this created a loop
				modelMapper = new ModelMapper();
				GameDto gameDto = modelMapper.map(game, GameDto.class);
				modelMapper.addMappings(new GameMapFromEntity()); // customer mapping
				gameDto.setName();
				gameDtos.add(gameDto);
			}
			playerDto.setGameDtos(gameDtos);
		} else {
			playerDto.setGameDtos(null);
		}
		
		List<CasinoDto> casinoDtos = new ArrayList<>();
		if (player.getCasinos() != null) {
			for (Casino casino: player.getCasinos()) {
				// casinoDtos.add(convertToDto(casino)); this created a loop
				modelMapper = new ModelMapper();
				CasinoDto casinoDto = modelMapper.map(casino, CasinoDto.class);
				//modelMapper.addMappings(new CasinoMapFromEntity()); // customer mapping
				casinoDtos.add(casinoDto);
			}
			playerDto.setCasinoDtos(casinoDtos);
		} else {
			playerDto.setCasinoDtos(null);
		}
		
		playerDto.setName();
		playerDto.setWinCount();
		
		return playerDto;
	}
	
	public Player convertToEntity(PlayerDto playerDto) throws ParseException {
		ModelMapper modelMapper = new ModelMapper();
		Player player = modelMapper.map(playerDto, Player.class);
		
		player.setGames(null);
		player.setCasinos(null);
		
		return player;
	}
	
	public GameDto convertToDto(Game game) {
		ModelMapper modelMapper = new ModelMapper();
		GameDto gameDto = modelMapper.map(game, GameDto.class);
		modelMapper.addMappings(new GameMapFromEntity()); // customer mapping
		
		gameDto.setName();
		if (game.getPlayer() != null) {
			// gameDto.setWinner(convertToDto(game.getPlayer())); // this created a loop...
			modelMapper = new ModelMapper();
			PlayerDto playerDto = modelMapper.map(game.getPlayer(), PlayerDto.class);
			playerDto.setGameDtos(null);
			playerDto.setName();
			playerDto.setWinCount();
			gameDto.setWinner(playerDto);
		} else {
			gameDto.setWinner(null);
		}
		gameDto.setCardsDealt();
		gameDto.setCardsLeft();
		return gameDto;
	}
	
	public Game convertToEntity(GameDto gameDto) throws ParseException {
		ModelMapper modelMapper = new ModelMapper();
		Game game = modelMapper.map(gameDto, Game.class);
		modelMapper.addMappings(new GameMapFromDto()); // customer mapping
		if (gameDto.getWinner() != null) {
			
			// game.setPlayer(convertToEntity(gameDto.getWinner())); // this creates a loop ..
			modelMapper = new ModelMapper();
			game.setPlayer(modelMapper.map(gameDto.getWinner(), Player.class));
		} else {
			game.setPlayer(null);
		}
		return game;
	}
	
	public DeckDto convertToDto(Deck deck) throws Exception {
		ModelMapper modelMapper = new ModelMapper();
		DeckDto deckDto = modelMapper.map(deck, DeckDto.class);
		
		if (deck.getDealtTo() != null) {
			//deckDto.setDealtToDto(convertToDto(deck.getDealtTo())); // does this create a loop ?
			deckDto.setDealtToDto(modelMapper.map(deck.getDealtTo(), CasinoDto.class));
		} else {
			deckDto.setDealtToDto(null);
		}
		if (deck.getGame() != null) {
			deckDto.setGameDto(convertToDto(deck.getGame()));
		} else {
			deckDto.setGameDto(null);
		}
		if (deck.getCard() != null) {
			deckDto.setCardDto(convertToDto(deck.getCard()));
		} else {
			deckDto.setCardDto(null);
		}
		
		deckDto.setName();
		return deckDto;
	}
	
	public Deck convertToEntity(DeckDto deckDto) throws ParseException {
		ModelMapper modelMapper = new ModelMapper();
		Deck deck = modelMapper.map(deckDto, Deck.class);
		
		if (deckDto.getDealtToDto() != null) {
			deck.setDealtTo(convertToEntity(deckDto.getDealtToDto())); // this creates a loop ..
		} else {
			deck.setDealtTo(null);
		}
		if (deckDto.getGameDto() != null) {
			deck.setGame(convertToEntity(deckDto.getGameDto())); // this creates a loop ..
		} else {
			deck.setGame(null);
		}
		if (deckDto.getCardDto() != null) {
			deck.setCard(convertToEntity(deckDto.getCardDto())); // this creates a loop ..
		} else {
			deck.setCard(null);
		}
		
		deck.setDealtTo(convertToEntity(deckDto.getDealtToDto())); // this creates a loop ..
		deck.setGame(convertToEntity(deckDto.getGameDto())); // this creates a loop ..
		deck.setCard(convertToEntity(deckDto.getCardDto())); // this creates a loop ..
		
		return deck;
	}
	
	public CasinoDto convertToDto(Casino casino) throws Exception {
		ModelMapper modelMapper = new ModelMapper();
		CasinoDto casinoDto = modelMapper.map(casino, CasinoDto.class);
		
		if (casino.getPlayer() != null) {
			if (casino.getPlayer() != null) {
				// casinoDto.setWinner(convertToDto(casino.getPlayer())); // this created a loop...
				modelMapper = new ModelMapper();
				PlayerDto playerDto = modelMapper.map(casino.getPlayer(), PlayerDto.class);
				playerDto.setCasinoDtos(null);
				playerDto.setName();
				playerDto.setWinCount();
				casinoDto.setPlayerDto(playerDto);
			} else {
				casinoDto.setPlayerDto(null);
			}
			
		} else {
			casinoDto.setPlayerDto(null);
		}
		if (casino.getGame() != null) {
			casinoDto.setGameDto(convertToDto(casino.getGame()));
		} else {
			casinoDto.setGameDto(null);
		}
		
		casinoDto.setName();
		casinoDto.setBalance();
		return casinoDto;
	}
	
	public Casino convertToEntity(CasinoDto casinoDto) throws ParseException {
		ModelMapper modelMapper = new ModelMapper();
		Casino casino = modelMapper.map(casinoDto, Casino.class);
		
		if (casinoDto.getPlayerDto() != null) {
			casino.setPlayer(convertToEntity(casinoDto.getPlayerDto())); // this creates a loop ..
		} else {
			casino.setPlayer(null);
		}
		if (casinoDto.getGameDto() != null) {
			casino.setGame(convertToEntity(casinoDto.getGameDto())); // this creates a loop ..
		} else {
			casino.setGame(null);
		}
		
		casino.setPlayer(convertToEntity(casinoDto.getPlayerDto())); // this creates a loop ..
		casino.setGame(convertToEntity(casinoDto.getGameDto())); // this creates a loop ..
		
		return casino;
	}
	
	public HandDto convertToDto(Hand hand) throws Exception {
		ModelMapper modelMapper = new ModelMapper();
		HandDto handDto = modelMapper.map(hand, HandDto.class);
		
		if (hand.getPlayer() != null) {
			handDto.setPlayerDto(convertToDto(hand.getPlayer())); // does this create a loop ?
			//handDto.setPlayerDto(modelMapper.map(hand.getPlayer(), PlayerDto.class));
		} else {
			handDto.setPlayerDto(null);
		}
		if (hand.getCasino() != null) {
			handDto.setCasinoDto(convertToDto(hand.getCasino()));
		} else {
			handDto.setCasinoDto(null);
		}
		if (hand.getCard() != null) {
			handDto.setCardDto(convertToDto(hand.getCard()));
		} else {
			handDto.setCardDto(null);
		}
		
		handDto.setName();
		return handDto;
	}
	
	public Hand convertToEntity(HandDto handDto) throws ParseException {
		ModelMapper modelMapper = new ModelMapper();
		Hand hand = modelMapper.map(handDto, Hand.class);
		
		if (handDto.getPlayerDto() != null) {
			hand.setPlayer(convertToEntity(handDto.getPlayerDto())); // this creates a loop ..
		} else {
			hand.setPlayer(null);
		}
		if (handDto.getCasinoDto() != null) {
			hand.setCasino(convertToEntity(handDto.getCasinoDto())); // this creates a loop ..
		} else {
			hand.setCasino(null);
		}
		if (handDto.getCardDto() != null) {
			hand.setCard(convertToEntity(handDto.getCardDto())); // this creates a loop ..
		} else {
			hand.setCard(null);
		}
		
		hand.setPlayer(convertToEntity(handDto.getPlayerDto())); // this creates a loop ..
		hand.setCasino(convertToEntity(handDto.getCasinoDto())); // this creates a loop ..
		hand.setCard(convertToEntity(handDto.getCardDto())); // this creates a loop ..
		
		return hand;
	}
	
	public CardDto convertToDto(Card card) throws Exception {
		ModelMapper modelMapper = new ModelMapper();
		CardDto cardDto = modelMapper.map(card, CardDto.class);
		cardDto.setSuit(card.getSuit());
		cardDto.setRank(card.getRank());
		cardDto.setValue(card.getValue());
		return cardDto;
	}
	
	public Card convertToEntity(CardDto cardDto) throws ParseException {
		ModelMapper modelMapper = new ModelMapper();
		Card card = modelMapper.map(cardDto, Card.class);
		return card;
	}
	
}
