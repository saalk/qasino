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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Qasino {
    private MessageDto message;
    private IdsDto ids = new IdsDto();
    private VisitorDto visitor;
    private GameDto game;
    private InvitationsDTO invitations;
    private LeagueDto league;
}
