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
@Slf4j
class GameMapperTest {

    static int INITIATOR_5 = 5;
    static Avatar ELF = Avatar.ELF;

    // create and inject mocked instances - no real object !!
    // without having to call Mockito.mock manually
    // - mocks do nothing when their methods are called !!
    // - doing a set will not change the null or zero !!
    // - a when().thenReturn() will set behaviour upfont but
    // - but a assertThat().isEqualTo() for the same is POINTLESS
    // - you are still able to verify number of calls on it however
    @Mock
    Visitor visitorMock;

    @Test
    void givenGame_whenMaps_thenProducesCorrectDto() {

        // given a Game for a mock league with 2 players and cards
        League leagueMock = League.buildDummy(visitorMock, "topLeague");
        Game realGame = Game.buildDummy(leagueMock, INITIATOR_5);

        Deck deck = DeckFactory.createShuffledDeck(realGame, 2);
        List<PlayingCard> playingCards = deck.getPlayingCards();
        List<Card> cards = new ArrayList<>();
        int i = 1;
        for (PlayingCard playingCard : playingCards) {
            Card card = new Card(playingCard.getRankAndSuit(), realGame, null, i++, Location.STOCK);
            cards.add(card);
        }
        realGame.setCards(cards);

        List<Player> players = new ArrayList<>();
        Player player1 = Player.buildDummyHuman(visitorMock, realGame, Avatar.MAGICIAN);
        Player player2 = Player.buildDummyHuman(visitorMock, realGame, ELF);
        players.add(player1);
        players.add(player2);
        // that has top 3 cards in hand - in total 13 cards exist so 10 in stock
        cards.get(0).setHand(player1);
        cards.get(0).setLocation(Location.HAND);
        cards.get(1).setHand(player2);
        cards.get(1).setLocation(Location.HAND);
        realGame.setCards(cards);
        realGame.setPlayers(players);

        // when calling the game mapper
        GameDto gameDto = GameMapper.INSTANCE.toDto(realGame);

        // then a new GameDto is created
        // - based on the given visitor and 2 players and cards

        // core
        assertEquals(gameDto.getGameId(), realGame.getGameId());

        // ref - visitor is mock so all is null, zero or false by initialization
        assertEquals(gameDto.getLeague().getName(), realGame.getLeague().getName());
        assertEquals(gameDto.getInitiator(), INITIATOR_5);
        assertEquals(gameDto.getCards().size(), realGame.getCards().size());
        assertEquals(gameDto.getCards().get(1).getRankSuit(), realGame.getCards().get(1).getRankSuit());
        assertEquals(gameDto.getPlayers().size(), realGame.getPlayers().size());
        assertEquals(gameDto.getPlayers().get(1).getAvatar(), ELF);

        // Normal fields
        assertEquals(gameDto.getState(), realGame.getState());
        assertEquals(gameDto.getPreviousState(), realGame.getPreviousState());
        assertEquals(gameDto.getType(), realGame.getType());
        assertEquals(gameDto.getStyle(), realGame.getStyle());
        assertEquals(gameDto.getAnte(), realGame.getAnte());

        assertEquals(gameDto.getYear(), realGame.getYear());
        assertEquals(gameDto.getMonth(), realGame.getMonth());
        assertEquals(gameDto.getWeek(), realGame.getWeek());
        assertEquals(gameDto.getWeekday(), realGame.getWeekday());

        // derived
        assertEquals(gameDto.getCardsInStock(), cardsInStock(cards));
        int stockDto = gameDto.getCardsInStock().length();
        int stock = (cardsInStock(realGame.getCards())).length();
        int low = 5;
        assertEquals(stockDto, stock);
        // "[xx],...,[xx]".length() is 55 => 11x4 = 44 + 10 = 54 or 55 when the '10' is inclused
        assertEquals(gameDto.getGameStateGroup(), realGame.getState().getGroup());
        // we dont map playing so this is always false here
        assertFalse(gameDto.isActivePlayerInitiator());

        Style style = Style.fromLabelWithDefault(realGame.getStyle());
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