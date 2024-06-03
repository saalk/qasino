package cloud.qasino.games.response;

import cloud.qasino.games.dto.enums.EnumOverview;
import cloud.qasino.games.dto.elements.*;
import cloud.qasino.games.dto.enums.Params;
import cloud.qasino.games.dto.statistics.Statistic;
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
public class QasinoResponse {

    @JsonProperty("Message")
    private String action;
    @JsonProperty("ShowMessage")
    private boolean actionNeeded;

    @JsonProperty("NavBarItems")
    private List<NavigationBarItem> navBarItems;

    // Params
    @JsonProperty("Params")
    private Params params = new Params();

    // Forms
    @JsonProperty("PageVisitor")
    private PageVisitor pageVisitor;
    @JsonProperty("PageGameSetup")
    private PageGameSetup pageGameSetup;
    @JsonProperty("PageGamePlay")
    private PageGamePlay pageGamePlay;
    @JsonProperty("PageGameInvitations")
    private PageGameInvitations pageGameInvitations;
    @JsonProperty("PageLeague")
    private PageLeague pageLeague;

    // extra
    @JsonProperty("EnumOverview")
    EnumOverview enumOverview = new EnumOverview();
    @JsonProperty("Statistics")
    List<Statistic> statistics;
}
