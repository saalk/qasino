package cloud.qasino.games.database.service;

import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.League;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.database.entity.enums.card.PlayingCard;
import cloud.qasino.games.database.entity.enums.game.GameState;
import cloud.qasino.games.database.entity.enums.game.Style;
import cloud.qasino.games.database.entity.enums.player.AiLevel;
import cloud.qasino.games.database.entity.enums.player.PlayerType;
import cloud.qasino.games.database.repository.CardRepository;
import cloud.qasino.games.database.repository.GameRepository;
import cloud.qasino.games.database.repository.PlayerRepository;
import cloud.qasino.games.database.security.Visitor;
import cloud.qasino.games.database.security.VisitorRepository;
import cloud.qasino.games.dto.model.GameDto;
import cloud.qasino.games.dto.model.GameShortDto;
import cloud.qasino.games.dto.model.LeagueDto;
import cloud.qasino.games.dto.model.PlayerDto;
import cloud.qasino.games.dto.model.PlayingDto;
import cloud.qasino.games.dto.mapper.GameMapper;
import cloud.qasino.games.dto.mapper.GameShortMapper;
import cloud.qasino.games.dto.mapper.LeagueMapper;
import cloud.qasino.games.dto.request.CreationDto;
import cloud.qasino.games.dto.request.ParamsDto;
import cloud.qasino.games.exception.MyBusinessException;
import cloud.qasino.games.pattern.factory.Deck;
import cloud.qasino.games.pattern.factory.DeckFactory;
import cloud.qasino.games.pattern.stream.StreamUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Lazy
public class GameService {

    // @formatter:off
    @Autowired private GameRepository gameRepository;
    @Autowired private VisitorRepository visitorRepository;
    @Autowired private CardRepository cardRepository;
    @Autowired private PlayerRepository playerRepository;
    @Autowired private PlayerServiceOld playerServiceOld;

    // counts

    // find one
    public GameDto findOneByGameId(ParamsDto paramsDto) {
        Game retrievedGame = gameRepository.getReferenceById(paramsDto.getSuppliedGameId());
        return GameMapper.INSTANCE.toDto(retrievedGame, retrievedGame.getCards());
    };
    public GameDto findLatestGameForVisitorId(ParamsDto paramsDto){
        if (paramsDto.getSuppliedGameId() > 0) {
            Optional<Game> foundGame = gameRepository.findById(paramsDto.getSuppliedGameId());
            if (foundGame.isPresent()) {
                return GameMapper.INSTANCE.toDto(foundGame.get(),foundGame.get().getCards());
            }
        }
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
        return GameMapper.INSTANCE.toDto(foundGame.get(0),foundGame.get(0).getCards());

    }
    public List<GameShortDto> findInvitedGamesForVisitorId(ParamsDto paramsDto){
        if (paramsDto.getSuppliedVisitorId() > 0) {
            Pageable pageable = PageRequest.of(0, 4);
            List<Game> foundGames = gameRepository.findAllNewGamesForVisitorWithPage(paramsDto.getSuppliedVisitorId(), pageable);
            if (foundGames.isEmpty()) return null;
            return GameShortMapper.INSTANCE.toDtoList(foundGames, null);
        }
        return new ArrayList<>();
    }
    public GameDto setupNewGameWithPlayerInitiator(CreationDto creation, long initiator) {
        Game game = new Game(
                null,
                creation.getSuppliedType().getLabel(),
                initiator,
                creation.getSuppliedStyle(),
                creation.getSuppliedAnte());
        // TODO
        List<Player> allPlayersForTheGame = playerRepository.findByGame(game);
        String avatarName = "avatarName";
        Visitor visitor = visitorRepository.getReferenceById(initiator);
        Player player = new Player(
                visitor,
                game,
                PlayerType.INITIATOR,
                visitor.getBalance(),
                allPlayersForTheGame.size()+1,
                creation.getSuppliedAvatar(),
                avatarName,
                AiLevel.HUMAN);
        Player newPlayer = playerRepository.save(player);
        Game newGame = gameRepository.save(game);
        return GameMapper.INSTANCE.toDto(newGame, newGame.getCards());
    }
    public GameDto updateStyleForGame(Style style, long gameId) {
        Game game = gameRepository.getReferenceById(gameId);
        game.setStyle(style.updateLabelFromEnums());
        Game newGame = gameRepository.save(game);
        return GameMapper.INSTANCE.toDto(newGame, newGame.getCards());
    }
    public GameDto updateStateForGame(GameState gameState, long gameId) {
        Game game = gameRepository.getReferenceById(gameId);
        game.setState(gameState);
        Game newGame = gameRepository.save(game);
        return GameMapper.INSTANCE.toDto(newGame, newGame.getCards());
    }
    public GameDto updatePlayingStateForGame(PlayerDto player, long gameId) {
        Game game = gameRepository.getReferenceById(gameId);

        if ((player.isHuman())) {
            if (game.getInitiator() == player.getPlayerId()) {
                if (game.getState() != GameState.INITIATOR_MOVE) {
                    game.setState(GameState.INITIATOR_MOVE);
                    Game updateGame = gameRepository.save(game);
                    return GameMapper.INSTANCE.toDto(updateGame, updateGame.getCards());
                } else {
                    if (game.getState() != GameState.INVITEE_MOVE) {
                        game.setState(GameState.INVITEE_MOVE);
                        Game updateGame = gameRepository.save(game);
                        return GameMapper.INSTANCE.toDto(updateGame, updateGame.getCards());
                    }
                }
            }
        } else {
            if (game.getState() != GameState.BOT_MOVE) {
                game.setState(GameState.BOT_MOVE);
                Game updateGame = gameRepository.save(game);
                return GameMapper.INSTANCE.toDto(updateGame, updateGame.getCards());
            }
        }
        return GameMapper.INSTANCE.toDto(game, game.getCards());
    }
    public GameDto prepareExistingGame(GameDto gameDto, LeagueDto leagueDto, String style, int ante) {
        Game game = GameMapper.INSTANCE.fromDto(gameDto);
        League league = LeagueMapper.INSTANCE.fromDto(leagueDto);
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
        return  GameMapper.INSTANCE.toDto(newGame,newGame.getCards());
    }
    public GameDto addAndShuffleCardsForAGame(GameDto gameDto) {
        Game game = GameMapper.INSTANCE.fromDto(gameDto);
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
        return GameMapper.INSTANCE.toDto(game,game.getCards());
    }
    public PlayerDto findNextPlayerForGame(GameDto game, PlayingDto playing) {
        int totalSeats = game.getPlayers().size();
        int currentSeat = 1;
        if (playing != null ) {
             currentSeat = playing.getCurrentSeatNumber();
        }
        if (totalSeats == 1 || currentSeat == totalSeats) {
            return game.getPlayers().get(0);
        }
        List<PlayerDto> sortedPlayers = StreamUtil.sortPlayerDtosOnSeatWithStream(game.getPlayers());
        return sortedPlayers.get((currentSeat - 1) + 1);
    }

}
