package cloud.qasino.games.dto.mapper;

import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.League;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.database.entity.enums.card.PlayingCard;
import cloud.qasino.games.database.entity.enums.game.Style;
import cloud.qasino.games.database.entity.enums.player.Avatar;
import cloud.qasino.games.database.security.Visitor;
import cloud.qasino.games.dto.GameDto;
import cloud.qasino.games.pattern.factory.Deck;
import cloud.qasino.games.pattern.factory.DeckFactory;
import cloud.qasino.games.simulator.QasinoSimulator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(MockitoExtension.class)
class GameMapperTest extends QasinoSimulator {

    @Test
    void givenSimulatedQasino_andMaps_thenProducesCorrectDto() {

        // core
        assertEquals(gameDto.getGameId(), game.getGameId());
        // ref
        if (gameDto.getLeague() != null) {
            assertEquals(gameDto.getLeague().getName(), game.getLeague().getName());
        }
        assertEquals(gameDto.getInitiator(), INITIATOR_5);
//        assertEquals(gameDto.getCards().size(), game.getCards().size());
//        assertEquals(gameDto.getCards().get(1).getRankSuit(), game.getCards().get(1).getRankSuit());
//        assertEquals(gameDto.getPlayers().size(), game.getPlayers().size());
//        assertEquals(gameDto.getPlayers().get(0).getAvatar(), HUMAN_AVATAR);
//        assertEquals(gameDto.getPlayers().get(1).getAvatar(), BOT_AVATAR);
        // normal fields
        assertEquals(gameDto.getState(), game.getState());
        assertEquals(gameDto.getPreviousState(), game.getPreviousState());
        assertEquals(gameDto.getType(), game.getType());
        assertEquals(gameDto.getStyle(), game.getStyle());
        assertEquals(gameDto.getAnte(), game.getAnte());
        assertEquals(gameDto.getYear(), game.getYear());
        assertEquals(gameDto.getMonth(), game.getMonth());
        assertEquals(gameDto.getWeek(), game.getWeek());
        assertEquals(gameDto.getWeekday(), game.getWeekday());
        // derived
        assertEquals(gameDto.getCardsInStock(), cardsInStock(game.getCards()));
        int stockDto = gameDto.getCardsInStock().length();
        int stock = (cardsInStock(game.getCards())).length();
        int low = 5;
        assertEquals(stockDto, stock);
        // "[xx],...,[xx]".length() is 55 => 11x4 = 44 + 10 = 54 or 55 when the '10' is inclused
        assertEquals(gameDto.getGameStateGroup(), game.getState().getGroup());
        // we dont map playing so this is always false here
        assertFalse(gameDto.isActivePlayerInitiator());
        Style style = Style.fromLabelWithDefault(game.getStyle());
        assertEquals(gameDto.getAnteToWin(), style.getAnteToWin());
        assertEquals(gameDto.getBettingStrategy(), style.getBettingStrategy());
        assertEquals(gameDto.getDeckConfiguration(), style.getDeckConfiguration());
        assertEquals(gameDto.getOneTimeInsurance(), style.getOneTimeInsurance());
        assertEquals(gameDto.getRoundsToWin(), style.getRoundsToWin());
        assertEquals(gameDto.getTurnsToWin(), style.getTurnsToWin());

    }

    private String cardsInStock(List<Card> hand) {
        List<String> handStrings =
                hand.stream()
                        .filter(location -> location.getLocation().equals(Location.STOCK))
                        .map(Card::getRankSuit)
                        .collect(Collectors.toList());
        return "[" + String.join("],[", handStrings) + "]";
    }
}