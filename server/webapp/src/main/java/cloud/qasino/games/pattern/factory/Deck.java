package cloud.qasino.games.pattern.factory;

import cloud.qasino.games.database.entity.enums.card.PlayingCard;
import lombok.Data;

import java.util.Collections;
import java.util.List;

import static cloud.qasino.games.database.entity.enums.card.PlayingCard.createDeckForRandomSuitWithXJokers;

@Data
public abstract class Deck implements DeckInterface {

    protected List<PlayingCard> playingCards;

}
