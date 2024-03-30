package cloud.qasino.games.dto.statistics;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
//@JsonIdentityInfo(generator = JSOGGenerator.class)
//@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Total {

    public int totalVisitors;
    public int totalGames;
    public int totalPlayers;
    public int totalLeagues;
    public int totalCards;

    @JsonProperty("SubTotalsGame")
    private SubTotalsGame subTotalsGames;

}