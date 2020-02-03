package cloud.qasino.card.dto.statistics;

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
public class Total {

    public int totalUsers;
    public int totalGames;
    public int totalPlayers;
    public int totalLeagues;
    public int totalCards;

    @JsonProperty("SubTotalsGame")
    private SubTotalsGame subTotalsGames;

}