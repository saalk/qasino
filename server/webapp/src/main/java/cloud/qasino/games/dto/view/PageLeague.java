package cloud.qasino.games.dto.view;

import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.League;
import cloud.qasino.games.database.entity.Result;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageLeague {

    // buttons
    // 1 select and update league details
    // - input is
    //      name
    // 2 create a new league
    // - input is
    //      name

    // Main - 1, 2
    @JsonProperty("ActiveLeaguesForVisitor")
    private List<League> activeLeagues;
    @JsonProperty("League")
    private League selectedLeague;
    @JsonProperty("AllGamesForLeague")
    private List<Game> leagueGames;

    // Stats
    @JsonProperty("ResultsForLeague")
    private List<Result> resultsForLeague;

    // Pending actions


}

