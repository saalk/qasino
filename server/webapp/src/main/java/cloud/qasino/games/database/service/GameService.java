package cloud.qasino.games.database.service;

import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.League;
import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.database.entity.enums.card.PlayingCard;
import cloud.qasino.games.database.entity.enums.game.GameState;
import cloud.qasino.games.database.entity.enums.game.Style;
import cloud.qasino.games.database.entity.enums.game.Type;
import cloud.qasino.games.database.entity.enums.player.Avatar;
import cloud.qasino.games.database.repository.CardRepository;
import cloud.qasino.games.database.repository.GameRepository;
import cloud.qasino.games.database.repository.PlayerRepository;
import cloud.qasino.games.database.security.Visitor;
import cloud.qasino.games.exception.MyBusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static cloud.qasino.games.database.entity.enums.card.PlayingCard.createDeckForRandomSuitWithXJokers;
import static cloud.qasino.games.database.entity.enums.card.PlayingCard.createDeckWithXJokers;

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

        List<PlayingCard> playingCards;
        switch (Style.fromLabelWithDefault(activeGame.getStyle()).getDeckConfiguration()) {
            case ALL_THREE_JOKERS -> playingCards = createDeckWithXJokers(3);
            case ALL_TWO_JOKERS -> playingCards = createDeckWithXJokers(2);
            case ALL_ONE_JOKER -> playingCards = createDeckWithXJokers(1);
            case ALL_NO_JOKER -> playingCards = createDeckWithXJokers(0);
            case RANDOM_SUIT_THREE_JOKERS -> playingCards = createDeckForRandomSuitWithXJokers(3);
            case RANDOM_SUIT_TWO_JOKERS -> playingCards = createDeckForRandomSuitWithXJokers(2);
            case RANDOM_SUIT_ONE_JOKER -> playingCards = createDeckForRandomSuitWithXJokers(1);
            case RANDOM_SUIT_NO_JOKER -> playingCards = createDeckForRandomSuitWithXJokers(0);
            default -> playingCards = createDeckWithXJokers(3);
        }
        Collections.shuffle(playingCards);
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