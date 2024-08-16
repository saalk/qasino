package cloud.qasino.games.dto.mapper;

import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.simulator.QasinoSimulator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class PlayerMapperTest extends QasinoSimulator {

    @Test
    void givenSimulatedQasino_andMaps_thenProducesCorrectDto() {

        // core
        assertEquals(playerVisitorDto.getPlayerId(), playerVisitor.getPlayerId());
        // ref
        assertTrue(playerVisitorDto.getVisitor().isRepayPossible());
        // normal fields
        assertEquals(playerVisitorDto.getSeat(), playerVisitor.getSeat());
        assertEquals(playerVisitorDto.isHuman(), playerVisitor.isHuman());
        assertEquals(playerVisitorDto.getPlayerType(), playerVisitor.getPlayerType());
        assertEquals(playerVisitorDto.getFiches(), playerVisitor.getFiches());
        assertEquals(playerVisitorDto.getAvatar(), playerVisitor.getAvatar());
        assertEquals(playerVisitorDto.getAvatarName(), playerVisitor.getAvatarName());
        assertEquals(playerVisitorDto.getAiLevel(), playerVisitor.getAiLevel());
        assertEquals(playerVisitorDto.isWinner(), playerVisitor.isWinner());
        // derived
        assertEquals(playerVisitorDto.getCardsInHand(), cardsInHand(game.getCards()));
        int handDto = playerVisitorDto.getCardsInHand().length();
        int hand = (cardsInHand(game.getCards())).length();
        assertEquals(handDto, hand);
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

    @Test
    void toDtoList() {
    }

}