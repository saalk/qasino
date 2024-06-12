package cloud.qasino.games.database.service;

import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.League;
import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.database.entity.enums.card.PlayingCard;
import cloud.qasino.games.database.entity.enums.game.GameState;
import cloud.qasino.games.database.entity.enums.player.Avatar;
import cloud.qasino.games.database.repository.CardRepository;
import cloud.qasino.games.database.repository.GameRepository;
import cloud.qasino.games.database.repository.PlayerRepository;
import cloud.qasino.games.dto.GameDto;
import cloud.qasino.games.dto.VisitorDto;
import cloud.qasino.games.dto.mapper.GameMapper;
import cloud.qasino.games.exception.MyBusinessException;
import cloud.qasino.games.pattern.factory.Deck;
import cloud.qasino.games.pattern.factory.DeckFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GameService {

    // @formatter:off
    @Autowired private GameRepository gameRepository;
    @Autowired private CardRepository cardRepository;
    @Autowired private PlayerRepository playerRepository;
    @Autowired private PlayerServiceOld playerServiceOld;
    GameMapper gameMapper;

    public GameDto setupNewGameWithPlayerInitiator(GameDto gameDto, VisitorDto visitorDto) {
        Game game = gameMapper.gameDtoToGame(gameDto);
        game.setInitiator(visitorDto.getVisitorId());
        Game newGame = gameRepository.save(game);
        // TODO
        playerServiceOld.addHumanVisitorPlayerToAGame(null, newGame, Avatar.ELF);
        return gameMapper.gameToGameDto(newGame);
    }

    public GameDto prepareExistingGame(GameDto gameDto, League league, String style, int ante) {
        Game game = gameMapper.gameDtoToGame(gameDto);
        // You cannot change the initiator or the type
        if (!(league == null)) {
            game.setLeague(league);
        }
        if (!(style == null)) {
            game.setStyle(style);
        }
        if (!(ante == 0)) {
            game.setAnte(ante);
        }
        game.setState(GameState.PREPARED);
        Game newGame = gameRepository.save(game);
        return  gameMapper.gameToGameDto(newGame);
    }

    public GameDto addAndShuffleCardsForAGame(GameDto gameDto) {
        Game game = gameMapper.gameDtoToGame(gameDto);
        if (!game.getCards().isEmpty())
            throw new MyBusinessException("addAndShuffleCardsForAGame", "this game already has cards [" + game.getGameId() + "]");
        Deck deck = DeckFactory.createShuffledDeck(game, 0);
        List<PlayingCard> playingCards = deck.getPlayingCards();
        List<Card> cards = new ArrayList<>();
        int i = 1;
        for (PlayingCard playingCard : playingCards) {
            Card card = new Card(playingCard.getRankAndSuit(), game, null, i++, Location.STOCK);
            cards.add(card);
            cardRepository.save(card);
        }
        game.setState(GameState.STARTED);
        game.setCards(cards);
        game = gameRepository.save(game);
        return gameMapper.gameToGameDto(game);
    }
}
