package cloud.qasino.games.database.service;

import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.CardMove;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.Turn;
import cloud.qasino.games.database.entity.enums.card.Face;
import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.database.entity.enums.card.PlayingCard;
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
    // @formatter:on

    public Turn dealCardToPlayer(Game activeGame, Turn activeTurn, Player humanOrBot, Move move, Face face, Location location, int howMany) {

        Card topCardInStock = getTopCardInStock(activeGame, howMany);
        if (topCardInStock == null) return null; // no cards left -> end the game

        // 1: create or update Turn
        if (activeTurn == null) {
            // round and move is 1 by default with new
            activeTurn = new Turn(activeGame, humanOrBot.getPlayerId());
            turnRepository.save(activeTurn);
        }
        // 2: create a CardMove
        int roundNo = activeTurn.getCurrentRoundNumber();
        int turnNo = activeTurn.getCurrentMoveNumber();
        int seatNo = humanOrBot.getSeat();
        String details = topCardInStock.getRankSuit();
        CardMove newMove = new CardMove(
                activeTurn,
                humanOrBot,
                topCardInStock.getCardId(),
                move,
                location,
                details);
        newMove.setSequence(roundNo, seatNo, turnNo);
        cardMoveRepository.save(newMove);
        // for future use
        //        List<CardMove> newCardMove = new ArrayList<>();
        //        newCardMove.add(newMove);
        // 3: change the card relate to the game and set it to the players hand
        Card cardDealt = topCardInStock;
        cardDealt.setLocation(location);
        cardDealt.setFace(face);
        cardDealt.setHand(humanOrBot);
        cardRepository.save(cardDealt);

        return turnRepository.getReferenceById(activeTurn.getTurnId());
    }

    public List<CardMove> getCardMovesForGame(Game activeGame) {
        Turn activeTurn = activeGame.getTurn();
        return cardMoveRepository.findByTurnOrderBySequenceAsc(activeTurn);
    }
    public int getValueLastCardMove(List<CardMove> cardMoves) {
        long previousCardId = cardMoves.get(cardMoves.size() - 1).getCardMoveId();
        Optional<Card> previousCard = cardRepository.findById(previousCardId);
        return previousCard.map(card -> PlayingCard.calculateValueWithDefaultHighlow(card.getRankSuit(), card.getGame().getType())).orElse(0);

    }
    // @formatter:off
    private static Card getTopCardInStock(Game activeGame, int howMany) {
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
        if (topCardInStock == null) return null;
        return topCardInStock;
    }
}
