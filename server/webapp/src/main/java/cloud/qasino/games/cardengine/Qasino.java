package cloud.qasino.games.cardengine;

import cloud.qasino.games.dto.GameDto;
import cloud.qasino.games.dto.LeagueDto;
import cloud.qasino.games.dto.VisitorDto;
import cloud.qasino.games.dto.request.IdsDto;
import cloud.qasino.games.dto.request.MessageDto;
import cloud.qasino.games.response.view.NavigationBarItem;
import cloud.qasino.games.dto.InvitationsDTO;
import cloud.qasino.games.response.view.enums.EnumOverview;
import cloud.qasino.games.response.view.statistics.Statistic;
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

    private MessageDto message;
    private List<NavigationBarItem> navBarItems;

    // IdsDto
    private IdsDto ids = new IdsDto();

    // Forms
    private VisitorDto visitor;
    private GameDto game;
    private InvitationsDTO invitations;
    private LeagueDto league;

    // extra
    EnumOverview enumOverview = new EnumOverview();
    List<Statistic> statistics;
}
