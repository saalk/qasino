package cloud.qasino.games.dto.navigation;

import cloud.qasino.games.database.entity.League;
import cloud.qasino.games.database.entity.Visitor;
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
public class NavigationLeague {

    private boolean hasLeague;      // icon is 1/2/3 -> position in league or no league

    @JsonProperty("CreatedLeagues")
    private List<League> leagues;
    // list of active Leagues to choose results from
    // + boolean youInitiated
    // + enddate
    // + num playing / total games

    @JsonProperty("VisitorForLeagues")
    private List<Visitor> visitors;
    // list of visitors and scores per league
    // + int percentageBotWins
    // + int percentageVisitorWins

    @JsonProperty("NewLeague")
    // creating is always possible
    private League newLeague;
}

