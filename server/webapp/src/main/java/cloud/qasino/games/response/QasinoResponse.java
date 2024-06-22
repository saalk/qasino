package cloud.qasino.games.response;

import cloud.qasino.games.pattern.singleton.OnlineVisitorsPerDay;
import cloud.qasino.games.response.view.NavigationBarItem;
import cloud.qasino.games.dto.InvitationsDTO;
import cloud.qasino.games.response.view.PageGamePlay;
import cloud.qasino.games.response.view.PageGameSetup;
import cloud.qasino.games.response.view.PageLeague;
import cloud.qasino.games.response.view.PageVisitor;
import cloud.qasino.games.response.view.enums.EnumOverview;
import cloud.qasino.games.response.view.enums.Params;
import cloud.qasino.games.response.view.statistics.Statistic;
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
    public void setAction(String action) {
        this.action = "[logons=" + OnlineVisitorsPerDay.getInstance().getOnlineVisitors() + "] " + action;
    }
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
    private InvitationsDTO invitationsDTO;
    @JsonProperty("PageLeague")
    private PageLeague pageLeague;

    // extra
    @JsonProperty("EnumOverview")
    EnumOverview enumOverview = new EnumOverview();
    @JsonProperty("Statistics")
    List<Statistic> statistics;
}
