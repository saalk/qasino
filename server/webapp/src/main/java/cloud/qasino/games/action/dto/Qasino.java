package cloud.qasino.games.action.dto;

import cloud.qasino.games.dto.GameDto;
import cloud.qasino.games.dto.LeagueDto;
import cloud.qasino.games.dto.PlayingDto;
import cloud.qasino.games.dto.VisitorDto;
import cloud.qasino.games.dto.request.CreationDto;
import cloud.qasino.games.dto.request.MessageDto;
import cloud.qasino.games.dto.request.ParamsDto;
import cloud.qasino.games.dto.InvitationsDto;
import cloud.qasino.games.pattern.singleton.OnlineVisitorsPerDay;
import cloud.qasino.games.response.view.NavigationBarItem;
import cloud.qasino.games.response.view.enums.EnumOverview;
import cloud.qasino.games.response.view.statistics.Statistic;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
public class Qasino
{
    private String action;
    public void setAction(String action) {
        this.action = "today's visitor count = " + OnlineVisitorsPerDay.getInstance().getOnlineVisitors() + " | " + action;
    }
    private boolean actionNeeded;

    private List<NavigationBarItem> navBarItems;

    private MessageDto message = new MessageDto();
    private ParamsDto params = new ParamsDto();
    private CreationDto creation = new CreationDto();

    private VisitorDto visitor;
    private LeagueDto league;
    private GameDto game;
    private PlayingDto playing;
    private InvitationsDto invitations;

    // extra
    EnumOverview enumOverview = new EnumOverview();
    List<Statistic> statistics;

}
