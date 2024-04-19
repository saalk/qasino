package cloud.qasino.games.dto.elements;

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
public class PageLeagues {

    private boolean visitorHasActiveLeagues;

    @JsonProperty("SelectedLeague")
    private League selectedLeague;

    @JsonProperty("ActiveLeagues")
    private List<League> activeLeagues;

    @JsonProperty("VisitorForLeagues")
    private List<Visitor> leagueVisitors;


}

