package cloud.qasino.card.dto;

import cloud.qasino.card.dto.enumrefs.TurnEnums;
import cloud.qasino.card.dto.enumrefs.GameEnums;
import cloud.qasino.card.dto.enumrefs.PlayerEnums;
import cloud.qasino.card.dto.enumrefs.CardEnums;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//@JsonIdentityInfo(generator = JSOGGenerator.class)
//@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Enums {

    @JsonProperty("GameEnums")
    private GameEnums game = new GameEnums();
    @JsonProperty("PlayerEnums")
    private PlayerEnums player = new PlayerEnums();
    @JsonProperty("CardEnums")
    private CardEnums card = new CardEnums();
    @JsonProperty("TurnEnums")
    private TurnEnums turn = new TurnEnums();

    public Enums() {

    }
}