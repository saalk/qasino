package cloud.qasino.games.dto;

import cloud.qasino.games.dto.enums.EnumOverview;
import cloud.qasino.games.dto.elements.*;
import cloud.qasino.games.dto.statistics.Statistics;
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
public class Qasino {

    @JsonProperty("NavBarItems")
    private List<NavigationBarItem> navBarItems;


    @JsonProperty("You")
    private PageVisitor pageVisitor;

    @JsonProperty("GameConfigurator")
    private PageGameConfigurator pageGameConfigurator;

    @JsonProperty("GamePlay")
    private PageGamePlay pageGamePlay;

    @JsonProperty("PendingGames")
    private PagePendingGames pagePendingGames;

    @JsonProperty("Leagues")
    private PageLeagues pageLeagues;

    // extra
    @JsonProperty("Enums")
    EnumOverview enumOverview = new EnumOverview();
    @JsonProperty("Statistics")
    Statistics statistics = new Statistics();


}
