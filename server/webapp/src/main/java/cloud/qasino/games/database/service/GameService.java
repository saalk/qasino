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
import cloud.qasino.games.dto.PlayerDto;
import cloud.qasino.games.dto.VisitorDto;
import cloud.qasino.games.dto.mapper.GameMapper;
import cloud.qasino.games.dto.mapper.PlayerMapper;
import cloud.qasino.games.dto.request.ParamsDto;
import cloud.qasino.games.exception.MyBusinessException;
import cloud.qasino.games.pattern.factory.Deck;
import cloud.qasino.games.pattern.factory.DeckFactory;
import cloud.qasino.games.pattern.stream.StreamUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@Lazy
public class GameService {

    // @formatter:off
    @Autowired private GameRepository gameRepository;
    @Autowired private CardRepository cardRepository;
    @Autowired private PlayerRepository playerRepository;
    @Autowired private PlayerServiceOld playerServiceOld;
    GameMapper gameMapper;
    PlayerMapper playerMapper;

    // counts

    // find one
    public GameDto findOneByGameId(ParamsDto paramsDto) {
        Game retrievedGame = gameRepository.findOneByGameId(paramsDto.getSuppliedVisitorId());
        return gameMapper.toDto(retrievedGame, retrievedGame.getCards());
    };
    public GameDto findLatestGameForVisitorId(ParamsDto paramsDto){
        Pageable pageable = PageRequest.of(0, 4);
        List<Game> foundGame = gameRepository.findAllNewGamesForVisitorWithPage(paramsDto.getSuppliedVisitorId(), pageable);
        if (foundGame.isEmpty()) {
            foundGame = gameRepository.findAllStartedGamesForVisitorWithPage(paramsDto.getSuppliedVisitorId(), pageable);
        }
        if (foundGame.isEmpty()) {
            foundGame = gameRepository.findAllFinishedGamesForVisitorWithPage(paramsDto.getSuppliedVisitorId(), pageable);
            // no games are present for the visitor
            if (foundGame.isEmpty()) return null;
        }
        // BR 2
        return gameMapper.toDto(foundGame.get(0),foundGame.get(0).getCards());

    }
    public GameDto setupNewGameWithPlayerInitiator(GameDto gameDto, VisitorDto visitorDto) {
        Game game = gameMapper.fromDto(gameDto);
        game.setInitiator(visitorDto.getVisitorId());
        Game newGame = gameRepository.save(game);
        // TODO
        playerServiceOld.addHumanVisitorPlayerToAGame(null, newGame, Avatar.ELF);
        return gameMapper.toDto(newGame, newGame.getCards());
    }
    public GameDto prepareExistingGame(GameDto gameDto, League league, String style, int ante) {
        Game game = gameMapper.fromDto(gameDto);
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
        return  gameMapper.toDto(newGame,newGame.getCards());
    }
    public GameDto addAndShuffleCardsForAGame(GameDto gameDto) {
        Game game = gameMapper.fromDto(gameDto);
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
        return gameMapper.toDto(game,game.getCards());
    }
    public PlayerDto findNextPlayerForGame(GameDto game) {
        int totalSeats = game.getPlayers().size();
//        TODO int currentSeat = game.getPlaying().getCurrentSeatNumber();
        int currentSeat = 1;
        if (totalSeats == 1 || currentSeat == totalSeats) {
            return game.getPlayers().get(0);
        }
        List<PlayerDto> sortedPlayers = StreamUtil.sortPlayerDtosOnSeatWithStream(game.getPlayers());
        return sortedPlayers.get((currentSeat - 1) + 1);
    }

}
