package cloud.qasino.games.dto.mapper;

import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.CardMove;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.Playing;
import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.dto.CardDto;
import cloud.qasino.games.dto.HandDto;
import cloud.qasino.games.dto.SeatDto;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(uses = {HandMapper.class})
public interface SeatMapper {

    // for testing and use in other mappers
    SeatMapper INSTANCE = Mappers.getMapper(SeatMapper.class);

    @Mapping(target = "playerId", source = "player", qualifiedByName = "playerId")
    @Mapping(target = "hands", source = "player", qualifiedByName = "hands")
    @Mapping(target = "cardsInHand", source = "player", qualifiedByName = "cardsInHand")
    @Mapping(target = "lastCardInHand", source = "player", qualifiedByName = "lastCardInHand")
    @Mapping(target = "seatPlaying", source = "player", qualifiedByName = "isSeatPlaying")
    @Mapping(target = "seatPlayerTheInitiator", source = "player", qualifiedByName = "isSeatPlayerTheInitiator")
    @Mapping(target = "seatCurrentBet", source = "player", qualifiedByName = "seatCurrentBet")
    @Mapping(target = "visitorId", source = "player", qualifiedByName = "visitorId")
    @Mapping(target = "username", source = "player", qualifiedByName = "username")
    @Mapping(target = "seatStartBalance", source = "player", qualifiedByName = "seatStartBalance")
    SeatDto toDto(Player player, @Context Playing playing);

    @Named("playerId")
    default long playerId(Player player, @Context Playing playing) {
        return player.getPlayerId();
    }

    @Named("hands")
    default List<HandDto> hands(Player player, @Context Playing playing) {
        List<HandDto> hands = new ArrayList<>();
        List<CardMove> cardMoves = new ArrayList<>();
        int seat = player.getSeat();
        int round = 0;
        // cardmoves should be ordered by sequence
        for (CardMove cardmove : playing.getCardMoves()) {
            // different seat so do not save cardmove and skip to next
            if (cardmove.getSeatFromSequence() != player.getSeat()) {
                if (!cardMoves.isEmpty()) {
                    // save previous seat
                    hands.add(HandMapper.INSTANCE.toDto(cardMoves, round, seat));
                    cardMoves = new ArrayList<>();
                }
                continue;
            }

            // cardmove has correct seat
            if (cardmove.getRoundFromSequence() != round) {
                // first or new round
                if (!cardMoves.isEmpty()) {
                    // save previous seat at previous round
                    hands.add(HandMapper.INSTANCE.toDto(cardMoves, round, seat));
                    cardMoves = new ArrayList<>();

                }
                // save cardmove for same seat and new round
                cardMoves.add(cardmove);
                round = cardmove.getRoundFromSequence();
            } else {
                // save cardmove for same seat and round
                cardMoves.add(cardmove);
            }
        }
        if (!cardMoves.isEmpty()) {
            hands.add(HandMapper.INSTANCE.toDto(cardMoves, round, seat));
        }
        return hands;
    }

    @Named("cardsInHand")
    default String cardsInHand(Player player, @Context Playing playing) {
        // player.getCards does not work!!
        // player.getGame().getCards() does not work!!
        List<Card> cards = playing.getGame().getCards();
        if (cards == null) return "[null]";
        if (cards.isEmpty()) return "[empty]";
        List<String> handStrings =
                cards.stream()
                        .filter(location -> location.getLocation().equals(Location.HAND))
                        .filter(hand -> hand.getHand().getPlayerId() == player.getPlayerId())
                        .map(Card::getRankSuit)
                        .collect(Collectors.toList());
        return "[" + String.join("],[", handStrings) + "]";
    }

    @Named("lastCardInHand")
    default String lastCardInHand(Player player, @Context Playing playing) {
        // player.getCards does not work!!
        // player.getGame().getCards() does not work!!
        List<Card> cards = playing.getGame().getCards();
        if (cards == null) return "[null]";
        if (cards.isEmpty()) return "[empty]";
        List<Card> cardsInHand =
                cards.stream()
                        .filter(location -> location.getLocation().equals(Location.HAND))
                        .filter(hand -> hand.getHand().getPlayerId() == player.getPlayerId())
                        .toList();
        if (cardsInHand.isEmpty()) return "[empty]";
        String lastCardInHand = cardsInHand.get(cardsInHand.size()-1).getRankSuit();
        return "[" + lastCardInHand + "]";
    }

    @Named("isSeatPlaying")
    default boolean seatPlaying(Player player, @Context Playing playing) {
        return player.getPlayerId() == playing.getPlayer().getPlayerId();
    }

    @Named("isSeatPlayerTheInitiator")
    default boolean seatPlayerTheInitiator(Player player, @Context Playing playing) {
        if (player.getVisitor() != null ) {
            return player.getVisitor().getVisitorId() == playing.getGame().getInitiator();
        } else {
            return false;
        }
    }

    @Named("seatCurrentBet")
    default int seatCurrentBet(Player player, @Context Playing playing) {
        return playing.getGame().getAnte();
    }

    @Named("visitorId")
    default long visitorId(Player player, @Context Playing playing) {
        if (player.getVisitor() == null) return 0; // bots are no visitor
        return player.getVisitor().getVisitorId();
    }

    @Named("username")
    default String username(Player player, @Context Playing playing) {
        if (player.getVisitor() == null) return ""; // bots are no visitor
        return player.getVisitor().getUsername();
    }

    @Named("seatStartBalance")
    default int seatStartBalance(Player player, @Context Playing playing) {
        if (player.getVisitor() == null) return 0; // bots are no visitor
        return player.getVisitor().getBalance();
    }
}
