package cloud.qasino.games.database.service;

import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.CardMove;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.League;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.Turn;
import cloud.qasino.games.database.entity.Visitor;
import cloud.qasino.games.database.entity.enums.card.Face;
import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.database.entity.enums.card.PlayingCard;
import cloud.qasino.games.database.entity.enums.game.GameState;
import cloud.qasino.games.database.entity.enums.game.Type;
import cloud.qasino.games.database.entity.enums.move.Move;
import cloud.qasino.games.database.entity.enums.player.AiLevel;
import cloud.qasino.games.database.entity.enums.player.Avatar;
import cloud.qasino.games.database.entity.enums.player.Role;
import cloud.qasino.games.database.repository.CardMoveRepository;
import cloud.qasino.games.database.repository.CardRepository;
import cloud.qasino.games.database.repository.GameRepository;
import cloud.qasino.games.database.repository.PlayerRepository;
import cloud.qasino.games.database.repository.TurnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static cloud.qasino.games.config.Constants.DEFAULT_PAWN_SHIP_BOT;

@Service
public class PlayService {

    @Autowired
    private TurnRepository turnRepository;
    @Autowired
    private CardMoveRepository cardMoveRepository;
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private PlayerRepository playerRepository;

    // @formatter:off
    public Turn dealCardToPlayer(Game activeGame, Turn activeTurn, Player humanOrBot, Move move, Face face, int howMany) {

        // determine card to deal
        // todo ignore howMany for now
        Card topCardInStock = null;
        for (int i = 0; i < activeGame.getCards().size(); i++) {
            // find first card without hand (aka player_id) then break
            if (activeGame.getCards().get(i).getHand() == null) {
                topCardInStock = activeGame.getCards().get(i);
                break;
            }
        }
        if (topCardInStock == null) return null; // no cards left -> end the game

        // update turn - create a turn if not exist yes for a new game
        if (activeTurn == null) {
            // round and move is 1 by default with new
            activeTurn = new Turn(activeGame, humanOrBot.getPlayerId());
        }

        int roundNo = activeTurn.getCurrentRoundNumber();
        int turnNo = activeTurn.getCurrentTurnNumber();
        int seatNo = humanOrBot.getSeat();
        String details = topCardInStock.getRankSuit();
        // register the card move to the turn
        CardMove newMove = new CardMove(
                activeTurn,
                humanOrBot,
                topCardInStock.getCardId(),
                move,
                Location.HAND,
                details);
        newMove.setSequence(roundNo, seatNo, turnNo);

        // for future use
//        List<CardMove> newCardMove = new ArrayList<>();
//        newCardMove.add(newMove);

        // save turn and cardMove
        turnRepository.save(activeTurn);
        cardMoveRepository.save(newMove);

        // change the cards moved
        Optional<Card> cardDealt = cardRepository.findById(newMove.getCardId());
        cardDealt.get().setLocation(Location.HAND);
        cardDealt.get().setFace(Face.UP);
        cardDealt.get().setHand(humanOrBot);
        cardRepository.save(cardDealt.get());

        return activeTurn;
    }
    public Game addCardsToGame(Game activeGame, int jokers) {
        if (!activeGame.getCards().isEmpty()) return activeGame;

        List<PlayingCard> playingCards = PlayingCard.newDeck(jokers);
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
    public Game setupNewGameWithPlayers(String type, Visitor initiator, League league, AiLevel aiLevel,
                                        String style, String ante, Avatar avatar) {
        Game newGame = gameRepository.save(new Game(
                league,
                type,
                initiator.getVisitorId(),
                style,
                Integer.parseInt(ante)));
        // todo move to find all entities with if
        // create human player for visitor with role initiator
        Player human = playerRepository.save(new Player(
                initiator,
                newGame,
                Role.INITIATOR,
                initiator.getBalance(),
                1,
                avatar,
                AiLevel.HUMAN));
        // create bot if ai level supplied
        if (aiLevel != null && aiLevel!=AiLevel.HUMAN) {
            int fiches = (int) (Math.random() * DEFAULT_PAWN_SHIP_BOT + 1);
            Player bot = playerRepository.save(new Player(
                    null,
                    newGame,
                    Role.BOT,
                    fiches,
                    2,
                    avatar,
                    aiLevel));
        }
        return newGame;
    }
    public Game prepareExistingGame(Game game, Visitor initiator, Type type, League league, String style, int ante) {
        // update game
        if (!(initiator == null)) {
            game.setInitiator(initiator.getVisitorId());
        }
        if (!(league == null)) {
            game.setLeague(league);
        }
        if (!(type == null)) {
            game.setType(type);
        }
        if (!(style.isEmpty())) {
            game.setStyle(style);
        }
        if (!(ante == 0)) {
            game.setAnte(ante);
        }
        game.setState(GameState.PREPARED);
        return gameRepository.save(game);
    }
    public List<CardMove> getCardMovesForGame(Game activeGame) {
        Turn activeTurn = activeGame.getTurn();
        return cardMoveRepository.findByTurnOrderBySequenceAsc(activeTurn);
    }
    public int getValueLastCardMove(List<CardMove> cardMoves) {
        long previousCardId = cardMoves.get( cardMoves.size()-1).getCardMoveId();
        Optional<Card> previousCard = cardRepository.findById(previousCardId);
        if (previousCard.isPresent()) {
//            PlayingCard.calculateValueWithDefaultHighlowFromRank(previousCard.get().getRankSuit())
        }

        return 0;
    }
    public List<CardMove> getCardMovesForPlayer(Player activePlayer) {
        return cardMoveRepository.findByplayerIdOrderBySequenceAsc(activePlayer.getPlayerId());
        }
    // @formatter:on

}