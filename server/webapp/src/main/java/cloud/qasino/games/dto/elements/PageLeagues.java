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

    private boolean visitorHasActiveLeagues;

    @JsonProperty("ActiveLeaguesForVisitor")
    private List<League> activeLeagues;

    @JsonProperty("SelectedLeague")
    private League selectedLeague;

    @JsonProperty("ResultsForSelectedLeague")
    private List<Result> resultsForLeague;

    @JsonProperty("AllGamesForLeague")
    private List<Game> leagueGames;


}

