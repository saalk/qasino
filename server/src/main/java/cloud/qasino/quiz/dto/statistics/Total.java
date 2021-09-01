package cloud.qasino.quiz.dto.statistics;

import cloud.qasino.quiz.dto.statistics.SubTotalsGame;
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
    public int totalQuizs;

    @JsonProperty("SubTotalsGame")
    private SubTotalsGame subTotalsGames;

}