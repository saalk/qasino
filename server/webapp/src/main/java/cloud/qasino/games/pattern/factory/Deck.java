package cloud.qasino.games.pattern.factory;

import cloud.qasino.games.database.entity.enums.card.PlayingCard;
import lombok.Data;

import java.util.List;

@Data
public abstract class Deck implements DeckInterface {

    protected List<PlayingCard> playingCards;

}
