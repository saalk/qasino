package cloud.qasino.games.dto.statistics;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
//@JsonIdentityInfo(generator = JSOGGenerator.class)
//@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Statistics {

    public int totalVisitors;
    public int totalGames;
    @JsonProperty("SubTotalsGame")
    private SubTotalsGame subTotalsGames;
    public int totalPlayers;
    public int totalLeagues;
    public int totalCards;

}