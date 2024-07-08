package cloud.qasino.games.dto.mapper;

import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.database.entity.enums.card.PlayingCard;
import cloud.qasino.games.database.entity.enums.player.Avatar;
import cloud.qasino.games.database.security.Visitor;
import cloud.qasino.games.dto.PlayerDto;
import cloud.qasino.games.pattern.factory.Deck;
import cloud.qasino.games.pattern.factory.DeckFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
@Slf4j
class PlayerMapperTest {

    // create and inject mocked instances - no real object !!
    // without having to call Mockito.mock manually
    // - mocks do nothing when their methods are called !!
    // - doing a set will not change the null or zero !!
    // - a when().thenReturn() will set behaviour upfont but
    // - but a assertThat().isEqualTo() for the same is POINTLESS
    // - you are still able to verify number of calls on it however
    @Mock
    Visitor visitorMock;

    // create a spy instance - this is still the real object !!
    // - unless you 'overrule' a method with doReturn().when().method();
    // - a when().thenReturn() will give real method call - so initiate upfront !!
    // - doReturn(resultsIWant).when(myClassSpy).method1(); - use for making a spy
    // - When stubbing a method using spies -> please use doReturn() family of methods.
    // - when(Object) would result in calling the actual method that can throw exceptions.
    @Spy
    Game gameSpy;

    @Test
    void givenPlayer_whenMaps_thenProducesCorrectDto() {

        // setup game with cards
//        gameSpy = Game.buildDummy(null,1);

        Deck deck = DeckFactory.createShuffledDeck(gameSpy, 2);
        List<PlayingCard> playingCards = deck.getPlayingCards();
        List<Card> cards = new ArrayList<>();
        int i = 1;
        for (PlayingCard playingCard : playingCards) {
            Card card = new Card(playingCard.getRankAndSuit(), gameSpy, null, i++, Location.STOCK);
            cards.add(card);
        }
//        gameSpy.setCards(cards);
//        doReturn(cards).when(gameSpy).getCards();

        // given a human player
        Player player = Player.buildDummyHuman(visitorMock, gameSpy, Avatar.MAGICIAN);

        // that has top 3 cards in hand
        cards.get(0).setHand(player);
        cards.get(0).setLocation(Location.HAND);
        cards.get(1).setHand(player);
        cards.get(1).setLocation(Location.HAND);
//        gameSpy.setCards(cards);
        doReturn(cards).when(gameSpy).getCards();

        player.setGame(gameSpy);

        // when calling the player mapper
        PlayerDto playerDto = PlayerMapper.INSTANCE.toDto(player, cards);

        // then a new PlayerDto is created
        // - based on the given player
        // - with ref to the given mocks

        // core
        assertEquals(playerDto.getPlayerId(), player.getPlayerId());

        // ref - visitor is mock so all is null, zero or false by initialization
        assertFalse(playerDto.getVisitor().isRepayPossible());
//        assertEquals(playerDto.getGame().getCards(),cards); // not possible bc of loop problem

        // Normal fields
        assertEquals(playerDto.getSeat(), player.getSeat());
        assertEquals(playerDto.isHuman(), player.isHuman());
        assertEquals(playerDto.getPlayerType(), player.getPlayerType());
        assertEquals(playerDto.getFiches(), player.getFiches());
        assertEquals(playerDto.getAvatar(), player.getAvatar());
        assertEquals(playerDto.getAvatarName(), player.getAvatarName());
        assertEquals(playerDto.getAiLevel(), player.getAiLevel());
        assertEquals(playerDto.isWinner(), player.isWinner());

        // derived from mapper vs private logic for cards in hand
        assertEquals(playerDto.getCardsInHand(), cardsInHand(gameSpy.getCards(), player));
        log.info("playerDto.getCardsInHand() is <{}> ", playerDto.getCardsInHand());
        assertEquals(playerDto.getCardsInHand().length(), 9, "playerDto.getCardsInHand() is <" + playerDto.getCardsInHand() + ">"); // "[xx],[xx]".length() is 9

    }

    private String cardsInHand(List<Card> cards, Player player) {
        List<String> handStrings =
                cards.stream()
                        .filter(location -> location.getLocation().equals(Location.HAND))
                        .filter(hand -> hand.getHand().getPlayerId() == player.getPlayerId())
                        .map(Card::getRankSuit)
                        .collect(Collectors.toList());
        return "[" + String.join("],[", handStrings) + "]";
    }

    @Test
    void toDtoList() {
    }

}