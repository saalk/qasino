package cloud.qasino.games.database.service;

import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.CardMove;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Turn;
import cloud.qasino.games.database.entity.enums.card.Face;
import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.database.entity.enums.card.PlayingCard;
import cloud.qasino.games.database.entity.enums.move.Move;
import cloud.qasino.games.database.repository.CardMoveRepository;
import cloud.qasino.games.database.repository.CardRepository;
import cloud.qasino.games.database.repository.PlayerRepository;
import cloud.qasino.games.database.repository.TurnRepository;
import cloud.qasino.games.dto.mapper.GameMapper;
import cloud.qasino.games.dto.mapper.PlayerMapper;
import cloud.qasino.games.dto.request.ParamsDto;
import cloud.qasino.games.pattern.stream.StreamUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Lazy
public class TurnAndCardMoveService {

    // @formatter:off
    @Autowired private TurnRepository turnRepository;
    @Autowired private CardMoveRepository cardMoveRepository;
    @Autowired private CardRepository cardRepository;
    @Autowired private PlayerRepository playerRepository;
    PlayerMapper playerMapper;
    GameMapper gameMapper;

    // find
    public Turn findByGameId(ParamsDto paramsDto) {
        List<Turn> turns = turnRepository.findByGameId(paramsDto.getSuppliedGameId());
        if (turns.isEmpty()) {
            // when game is quit before started
            return null;
        }
        // FROM TURN
        return turns.get(0);
    }

    // card(s) related functions
    public List<Card> dealNCardsFromStockToActivePlayerForGame(Game activeGame, Move move, Location oldLocation, Location newLocation, Face face, int howMany) {
        Turn activeTurn = activeGame.getTurn();
        List<Card> topCardsInStock = getTopNCardsByLocationForGame(activeGame, oldLocation, howMany);
        List<Card> cardsDealt = new ArrayList<>();
        for (Card card : topCardsInStock) {
            Card cardDealt = card;
            cardDealt.setLocation(newLocation);
            cardDealt.setFace(face);
            cardDealt.setHand(activeTurn.getActivePlayer());
            cardsDealt.add(cardRepository.save(cardDealt));
            storeCardMoveForTurn(activeTurn,cardDealt,move,newLocation);
        }
        return cardsDealt;
    }
    private static List<Card> getTopNCardsByLocationForGame(Game activeGame, Location location, int howMany) {
        List<Card> orderedCards = StreamUtil.sortCardsOnSequenceWithStream(activeGame.getCards(),location);
        return StreamUtil.findFirstNCards(orderedCards, howMany);
    }

    public void storeCardMoveForTurn(Turn activeTurn, Card cardMoved, Move move, Location location) {
            CardMove newMove = new CardMove(
                    activeTurn,
                    activeTurn.getActivePlayer(),
                    cardMoved.getCardId(),
                    move,
                    location,
                    cardMoved.getRankSuit());
            newMove.setSequence(
                    activeTurn.getCurrentRoundNumber(),
                    activeTurn.getActivePlayer().getSeat(),
                    activeTurn.getCurrentMoveNumber());
            cardMoveRepository.save(newMove);
    }
    public List<CardMove> findCardMovesForGame(Game activeGame) {
        Turn activeTurn = activeGame.getTurn();
        return cardMoveRepository.findByTurnOrderBySequenceAsc(activeTurn);
    }
    public int getValueLastCardMove(List<CardMove> cardMoves) {
        long previousCardId = cardMoves.get(cardMoves.size() - 1).getCardMoveId();
        Optional<Card> previousCard = cardRepository.findById(previousCardId);
        return previousCard.map(card -> PlayingCard.calculateValueWithDefaultHighlow(card.getRankSuit(), card.getGame().getType())).orElse(0);
    }

}
