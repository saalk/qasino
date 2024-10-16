package cloud.qasino.games.dto.mapper;

import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.simulator.QasinoSimulator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class SeatMapperTest extends QasinoSimulator {

    @Test
    void givenSimulatedQasino_andMaps_thenProducesCorrectDto() {

        // FOR playerVisitor

        // core
        assertEquals(playerVisitorSeatDto.getPlayerId(), playerVisitor.getPlayerId());
        // ref
        // normal fields
        assertEquals(playerVisitorSeatDto.getSeatId(), playerVisitor.getSeat());
        assertEquals(playerVisitorSeatDto.isHuman(), playerVisitor.isHuman());
        assertEquals(playerVisitorSeatDto.getPlayerType(), playerVisitor.getPlayerType());
        assertEquals(playerVisitorSeatDto.getFiches(), playerVisitor.getFiches());
        assertEquals(playerVisitorSeatDto.getAvatar(), playerVisitor.getAvatar());
        assertEquals(playerVisitorSeatDto.getAvatarName(), playerVisitor.getAvatarName());
        assertEquals(playerVisitorSeatDto.getAiLevel(), playerVisitor.getAiLevel());
        assertEquals(playerVisitorSeatDto.isWinner(), playerVisitor.isWinner());
        // derived
        assertEquals(playerVisitorSeatDto.getCardsInHand(), cardsInHand(game.getCards()));
        int handDto = playerVisitorSeatDto.getCardsInHand().length();
        int hand = (cardsInHand(game.getCards())).length();
        assertEquals(handDto, hand);

        // FOR bot

        // core
        assertEquals(botSeatDto.getPlayerId(), bot.getPlayerId());


    }

    private String cardsInHand(List<Card> cards) {
        List<String> handStrings =
                cards.stream()
                        .filter(location -> location.getLocation().equals(Location.HAND))
                        .filter(hand -> hand.getHand().getPlayerId() == playerVisitor.getPlayerId())
                        .map(Card::getRankSuit)
                        .collect(Collectors.toList());
        return "[" + String.join("],[", handStrings) + "]";
    }

}