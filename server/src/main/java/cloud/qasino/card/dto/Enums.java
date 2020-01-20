package cloud.qasino.card.dto;

import cloud.qasino.card.dto.enumrefs.EventEnums;
import cloud.qasino.card.dto.enumrefs.GameEnums;
import cloud.qasino.card.dto.enumrefs.PlayerEnums;
import cloud.qasino.card.dto.enumrefs.PlayingCardEnums;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//@JsonIdentityInfo(generator = JSOGGenerator.class)
//@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Enums {

    private GameEnums game = new GameEnums();
    private PlayerEnums player = new PlayerEnums();
    private PlayingCardEnums playingCard = new PlayingCardEnums();
    private EventEnums event = new EventEnums();

    public Enums() {

    }
}