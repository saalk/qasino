package cloud.qasino.games.response.view.enums;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Data
@ToString
//@JsonIdentityInfo(generator = JSOGGenerator.class)
//@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class EnumOverview {

    @JsonProperty("GameEnums")
    private GameEnums game = new GameEnums();
    @JsonProperty("PlayerEnums")
    private PlayerEnums player = new PlayerEnums();
    @JsonProperty("CardEnums")
    private CardEnums card = new CardEnums();
    @JsonProperty("PlayingEnums")
    private PlayingEnums playing = new PlayingEnums();

    public EnumOverview() {

    }
}