package cloud.qasino.games.action.dto;

import cloud.qasino.games.dto.GameDto;
import cloud.qasino.games.dto.InvitationsDto;
import cloud.qasino.games.dto.LeagueDto;
import cloud.qasino.games.dto.PlayingDto;
import cloud.qasino.games.dto.ResultDto;
import cloud.qasino.games.dto.VisitorDto;
import cloud.qasino.games.dto.request.CreationDto;
import cloud.qasino.games.dto.request.MessageDto;
import cloud.qasino.games.dto.request.ParamsDto;
import cloud.qasino.games.response.view.NavigationBarItem;
import cloud.qasino.games.response.view.enums.EnumOverview;
import cloud.qasino.games.response.view.statistics.Statistic;
import jakarta.validation.Valid;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Qasino {

    // formatting:off
    private List<NavigationBarItem> navBarItems;
    private MessageDto message = new MessageDto();
    private ParamsDto params = new ParamsDto();
    @Valid
    private CreationDto creation = new CreationDto();
    private VisitorDto visitor;
    private GameDto game;
    private PlayingDto playing;
    private List<ResultDto> results;
    private InvitationsDto invitations;
    private LeagueDto league;
    EnumOverview enumOverview = new EnumOverview();
    List<Statistic> statistics = new ArrayList<>();

}
