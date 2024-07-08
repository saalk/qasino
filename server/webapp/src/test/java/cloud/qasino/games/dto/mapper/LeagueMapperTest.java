package cloud.qasino.games.dto.mapper;

import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.League;
import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.database.entity.enums.card.PlayingCard;
import cloud.qasino.games.database.security.Visitor;
import cloud.qasino.games.dto.LeagueDto;
import cloud.qasino.games.pattern.factory.Deck;
import cloud.qasino.games.pattern.factory.DeckFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@Slf4j
class LeagueMapperTest {

    // create and inject mocked instances - no real object !!
    // without having to call Mockito.mock manually
    // - mocks do nothing when their methods are called !!
    // - doing a set will not change the null or zero !!
    // - a when().thenReturn() will set behaviour upfont but
    // - but a assertThat().isEqualTo() for the same is POINTLESS
    // - you are still able to verify number of calls on it however
    @Mock
    Visitor visitorMock;

    Game realGame1 = Game.buildDummy(null, 1);
    Game realGame2 = Game.buildDummy(null, 1);

    @Test
    void givenLeague_whenMaps_thenProducesCorrectDto() {

        // setup 2 games with cards
        Deck deck = DeckFactory.createShuffledDeck(realGame1, 2);
        List<PlayingCard> playingCards = deck.getPlayingCards();
        List<Card> cards = new ArrayList<>();
        int i = 1;
        for (PlayingCard playingCard : playingCards) {
            Card card = new Card(playingCard.getRankAndSuit(), realGame1, null, i++, Location.STOCK);
            cards.add(card);
        }
        realGame1.setCards(cards);
        realGame2.setCards(cards);
        List<Game> games = new ArrayList<>();
        games.add(realGame1);
        games.add(realGame2);

        // given a League for a visitor with 2 games
        League league = League.buildDummy(visitorMock, "topLeague");
        league.setGames(games);

        // when calling the league mapper
        LeagueDto leagueDto = LeagueMapper.INSTANCE.toDto(league);

        // then a new LeagueDto is created
        // - based on the given visitor and games

        // core
        assertEquals(leagueDto.getLeagueId(), league.getLeagueId());

        // ref - visitor is mock so all is null, zero or false by initialization
        assertEquals(leagueDto.getVisitor().getAlias(), league.getVisitor().getAlias());
        assertEquals(leagueDto.getGames().get(1).getCards(), games.get(1).getCards());

        // Normal fields
        assertEquals(leagueDto.getName(), league.getName());
        assertEquals(leagueDto.getNameSequence(), league.getNameSequence());
        assertEquals(leagueDto.isActive(), league.isActive());

        // derived
    }
}