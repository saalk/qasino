package cloud.qasino.games.database.service;

import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.CardMove;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.Playing;
import cloud.qasino.games.database.entity.Result;
import cloud.qasino.games.database.entity.enums.card.Face;
import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.database.entity.enums.card.PlayingCard;
import cloud.qasino.games.database.entity.enums.game.Style;
import cloud.qasino.games.database.entity.enums.move.Move;
import cloud.qasino.games.database.repository.CardMoveRepository;
import cloud.qasino.games.database.repository.CardRepository;
import cloud.qasino.games.database.repository.GameRepository;
import cloud.qasino.games.database.repository.PlayingRepository;
import cloud.qasino.games.database.repository.ResultsRepository;
import cloud.qasino.games.dto.PlayingDto;
import cloud.qasino.games.dto.ResultDto;
import cloud.qasino.games.dto.SeatDto;
import cloud.qasino.games.dto.mapper.PlayingMapper;
import cloud.qasino.games.dto.mapper.ResultMapper;
import cloud.qasino.games.dto.mapper.SeatMapper;
import cloud.qasino.games.dto.request.ParamsDto;
import cloud.qasino.games.exception.MyNPException;
import cloud.qasino.games.pattern.statemachine.event.PlayEvent;
import cloud.qasino.games.pattern.stream.StreamUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class PlayingService {

    // @formatter:off
    @Autowired private PlayingRepository playingRepository;
    @Autowired private ResultsRepository resultsRepository;
    @Autowired private CardMoveRepository cardMoveRepository;
    @Autowired private CardRepository cardRepository;
    @Autowired private GameRepository gameRepository;

    // finds
    public PlayingDto findByGameId(ParamsDto paramsDto) {
        List<Playing> playings = playingRepository.findByGameId(paramsDto.getSuppliedGameId());
        if (playings.isEmpty()) {
            // when game is quit before started
            return null;
        }
        // FROM PLAYING
        return PlayingMapper.INSTANCE.toDto(playings.get(0));
    }
    public List<ResultDto> findResultsByGameId(ParamsDto paramsDto) {
        List<Result> results = resultsRepository.findByGameId(paramsDto.getSuppliedGameId());
        return ResultMapper.INSTANCE.toDtoList(results);
    }

    public List<SeatDto> findByPlayingOrGameId(ParamsDto paramsDto) {
        List<Playing> playings = playingRepository.findByGameId(paramsDto.getSuppliedGameId());
        Playing playing;
        Game game;
        if (playings.isEmpty()) {
            game = gameRepository.getReferenceById(paramsDto.getSuppliedGameId());
            playing = game.getPlaying();

        } else {
            playing = playings.get(0);
            game = playing.getGame();
        }
        List<SeatDto> seats = new ArrayList<>();

        for (Player player : game.getPlayers()) {
            seats.add(SeatMapper.INSTANCE.toDto(player, playing));
        }
        return seats;
    }

    public List<CardMove> findCardMovesForGame(Game game) {
        Playing playing = game.getPlaying();
        if (playing == null) {
            // when game is quit before started
            return null;
        }
        return cardMoveRepository.findByPlayingOrderBySequenceAsc(playing);
    }

    // save CARD & CARDMOVE
//    @Transactional
    public void dealCardsToPlayer(Game game, Move move, Location oldLocation, Location newLocation, Face face, int howMany) {
        Playing activePlaying = game.getPlaying();
        log.warn("activePlaying <{}", activePlaying);
        Player player = activePlaying.getPlayer();
        log.warn("player <{}", player);
        List<Card> topCardsInStock = getTopNCardsByLocationForGame(game, oldLocation, howMany);
        log.warn("topCardsInStock <{}>", topCardsInStock);
        List<Card> cardsDealt = new ArrayList<>();

        for (Card card : topCardsInStock) {
            Card cardDealt = card;
            log.warn("card <{}>", card);

            cardDealt.setLocation(newLocation);
            cardDealt.setFace(face);
            cardDealt.setHand(player);
            log.warn("cardDealt <{}>", cardDealt);
            cardsDealt.add(cardRepository.save(cardDealt));
            storeCardMoveForPlaying(activePlaying,cardDealt,move,newLocation);
        }
    }
    private static List<Card> getTopNCardsByLocationForGame(Game game, Location location, int howMany) {
        List<Card> orderedCards = StreamUtil.sortCardsOnSequenceWithStream(game.getCards(),location);
        return StreamUtil.findFirstNCards(orderedCards, howMany);
    }
    private void storeCardMoveForPlaying(Playing activePlaying, Card cardMoved, Move move, Location location) {
        CardMove newMove = new CardMove(
                activePlaying,
                activePlaying.getPlayer(),
                cardMoved.getCardId(),
                move,
                location,
                cardMoved.getRankSuit());
        newMove.setSequence(
                activePlaying.getCurrentRoundNumber(),
                activePlaying.getPlayer().getSeat(),
                activePlaying.getCurrentMoveNumber());
        log.warn("newMove <{}>", newMove);
        cardMoveRepository.save(newMove);
    }

    // CARDMOVE
    public int getValueLastCardMove(List<CardMove> cardMoves) {
        long previousCardId = cardMoves.get(cardMoves.size() - 1).getCardMoveId();
        Optional<Card> previousCard = cardRepository.findById(previousCardId);
        return previousCard.map(card -> PlayingCard.calculateValueWithDefaultHighlow(card.getRankSuit(), card.getGame().getType())).orElse(0);
    }

    // PLAYING
    public void updatePlaying(Move move, Playing activePlaying, Player nextPlayer) {
        if (move == Move.PASS) move = Move.DEAL;
        switch (move) {
            case HIGHER, LOWER -> {
                activePlaying.setCurrentSeatNumber(activePlaying.getPlayer().getSeat());
                activePlaying.setCurrentMoveNumber(activePlaying.getCurrentMoveNumber() + 1);
            }
            case DEAL -> {
                activePlaying.setCurrentSeatNumber(nextPlayer.getSeat());
                activePlaying.setCurrentMoveNumber(1);
                activePlaying.setPlayer(nextPlayer);
            }
            default -> throw new MyNPException("PlayNext", "updateActivePlaying [" + move + "]");
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
    public static Move mapPlayEventToMove(PlayEvent playEvent) {
        switch (playEvent) {
            case HIGHER -> {
                return Move.HIGHER;
            }
            case LOWER -> {
                return Move.LOWER;
            }
            case PASS -> {
                return Move.DEAL;
            }
            default -> throw new MyNPException("PlayNext", "playEvent [" + playEvent + "]");
        }
    }


}
