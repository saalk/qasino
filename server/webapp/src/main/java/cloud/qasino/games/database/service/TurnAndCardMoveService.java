package cloud.qasino.games.database.service;

import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.CardMove;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.Turn;
import cloud.qasino.games.database.entity.enums.card.Face;
import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.database.entity.enums.move.Move;
import cloud.qasino.games.database.repository.CardMoveRepository;
import cloud.qasino.games.database.repository.CardRepository;
import cloud.qasino.games.database.repository.TurnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TurnAndCardMoveService {

    // @formatter:off
    @Autowired private TurnRepository turnRepository;
    @Autowired private CardMoveRepository cardMoveRepository;
    @Autowired private CardRepository cardRepository;

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

}