package cloud.qasino.games.dto.elements;

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
public class PageLeagues {

    // Main
    @JsonProperty("SelectedLeague")
    private League selectedLeague;
    // Stats
    @JsonProperty("ResultsForSelectedLeague")
    private List<Result> resultsForLeague;
    @JsonProperty("ActiveLeaguesForVisitor")
    private List<League> activeLeagues;
    // Pending actions
    @JsonProperty("AllGamesForLeague")
    private List<Game> leagueGames;

}

