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
import cloud.qasino.games.database.security.Visitor;
import cloud.qasino.games.exception.MyBusinessException;
import cloud.qasino.games.pattern.factory.Deck;
import cloud.qasino.games.pattern.factory.DeckFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class GameService {

    // @formatter:off
    @Autowired private GameRepository gameRepository;
    @Autowired private CardRepository cardRepository;
    @Autowired private PlayerRepository playerRepository;
    @Autowired private PlayerService playerService;

    // Since Java 8, there are several static methods added to the Comparator interface
    // that can take lambda expressions to create a Comparator object.
    // We can use its comparing() method to construct a Comparator

    // This Comparator can compare seats and can be passed to a compare function
    public static Comparator<Game> gameAnteComparator() {
        return Comparator.comparing(Game::getAnte);
    }

    public Game setupNewGameWithPlayerInitiator(String type, Visitor initiator, League league, String style, String ante, Avatar avatar) {
        Game newGame = gameRepository.save(new Game(
                league,
                type,
                initiator.getVisitorId(),
                style,
                Integer.parseInt(ante)));
        playerService.addHumanVisitorPlayerToAGame(initiator, newGame, avatar);
        return gameRepository.getReferenceById(newGame.getGameId());
    }
    public Game prepareExistingGame(Game game, League league, String style, int ante) {
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
        return gameRepository.save(game);
    }
    public Game addAndShuffleCardsForAGame(Game activeGame) {
        if (!activeGame.getCards().isEmpty())
            throw new MyBusinessException("addAndShuffleCardsForAGame", "this game already has cards [" + activeGame.getGameId() + "]");

        Deck deck = DeckFactory.createShuffledDeck(activeGame, 0);
        List<PlayingCard> playingCards = deck.getPlayingCards();

        List<Card> cards = new ArrayList<>();
        int i = 1;
        for (PlayingCard playingCard : playingCards) {
            Card card = new Card(playingCard.getRankAndSuit(), activeGame, null, i++, Location.STOCK);
            cards.add(card);
            cardRepository.save(card);
        }
        activeGame.setState(GameState.STARTED);
        activeGame.setCards(cards);
        activeGame = gameRepository.save(activeGame);
        return activeGame;
    }

}
