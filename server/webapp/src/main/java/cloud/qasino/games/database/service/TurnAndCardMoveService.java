package cloud.qasino.games.database.service;

import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.CardMove;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.Turn;
import cloud.qasino.games.database.entity.enums.card.Face;
import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.database.entity.enums.card.PlayingCard;
import cloud.qasino.games.database.entity.enums.game.Style;
import cloud.qasino.games.database.entity.enums.move.Move;
import cloud.qasino.games.database.repository.CardMoveRepository;
import cloud.qasino.games.database.repository.CardRepository;
import cloud.qasino.games.database.repository.PlayerRepository;
import cloud.qasino.games.database.repository.TurnRepository;
import cloud.qasino.games.dto.mapper.GameMapper;
import cloud.qasino.games.dto.mapper.PlayerMapper;
import cloud.qasino.games.dto.request.ParamsDto;
import cloud.qasino.games.exception.MyNPException;
import cloud.qasino.games.pattern.statemachine.event.TurnEvent;
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

    // finds
    public Turn findByGameId(ParamsDto paramsDto) {
        List<Turn> turns = turnRepository.findByGameId(paramsDto.getSuppliedGameId());
        if (turns.isEmpty()) {
            // when game is quit before started
            return null;
        }
        // FROM TURN
        return turns.get(0);
    }
    public List<CardMove> findCardMovesForGame(Game activeGame) {
        Turn activeTurn = activeGame.getTurn();
        return cardMoveRepository.findByTurnOrderBySequenceAsc(activeTurn);
    }

    // save CARD & CARDMOVE
    public void dealCardsToActivePlayer(Game activeGame, Move move, Location oldLocation, Location newLocation, Face face, int howMany) {
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
        }
    private static List<Card> getTopNCardsByLocationForGame(Game activeGame, Location location, int howMany) {
        List<Card> orderedCards = StreamUtil.sortCardsOnSequenceWithStream(activeGame.getCards(),location);
        return StreamUtil.findFirstNCards(orderedCards, howMany);
    }
    private void storeCardMoveForTurn(Turn activeTurn, Card cardMoved, Move move, Location location) {
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

    // CARDMOVE
    public int getValueLastCardMove(List<CardMove> cardMoves) {
        long previousCardId = cardMoves.get(cardMoves.size() - 1).getCardMoveId();
        Optional<Card> previousCard = cardRepository.findById(previousCardId);
        return previousCard.map(card -> PlayingCard.calculateValueWithDefaultHighlow(card.getRankSuit(), card.getGame().getType())).orElse(0);
    }

    // TURN
    public void updateCurrentTurn(Move move, Turn activeTurn, Player nextPlayer) {
        switch (move) {
            case HIGHER, LOWER -> {
                activeTurn.setCurrentSeatNumber(activeTurn.getActivePlayer().getSeat());
                activeTurn.setCurrentMoveNumber(activeTurn.getCurrentMoveNumber() + 1);
            }
            case DEAL -> {
                activeTurn.setCurrentSeatNumber(nextPlayer.getSeat());
                activeTurn.setCurrentMoveNumber(1);
                activeTurn.setActivePlayer(nextPlayer);
            }
            default -> throw new MyNPException("PlayNext", "updateActiveTurn [" + move + "]");
        }
    }

    // STYLE
    public static boolean isRoundEqualToRoundsToWin(Style style, int round) {
        switch (style.getRoundsToWin()) {
            case ONE_ROUND -> {
                if (round == 2) {
                    return true;
                }
            }
            case TWO_ROUNDS -> {
                if (round == 3) {
                    return true;
                }
            }
            case THREE_ROUNDS -> {
                if (round == 4) {
                    return true;
                }
            }
            default -> throw new MyNPException("PlayNext", "isRoundEqualToRoundsToWin [" + style + "]");
        }
        return false;
    }
    public static boolean isMoveEqualToTurnsToWin(Style style, int move) {
        switch (style.getTurnsToWin()) {
            case ONE_WINS -> {
                if (move == 2) {
                    return true;
                }
            }
            case TWO_IN_A_ROW_WINS -> {
                if (move == 3) {
                    return true;
                }
            }
            case THREE_IN_A_ROW_WINS -> {
                if (move == 4) {
                    return true;
                }
            }
            default -> throw new MyNPException("PlayNext", "isMoveEqualToTurnsToWin [" + style + "]");
        }
        return false;
    }
    public static Move mapTurnEventToMove(TurnEvent turnEvent) {
        switch (turnEvent) {
            case HIGHER -> {
                return Move.HIGHER;
            }
            case LOWER -> {
                return Move.LOWER;
            }
            case PASS -> {
                return Move.DEAL;
            }
            default -> throw new MyNPException("PlayNext", "turnEvent [" + turnEvent + "]");
        }
    }


}
