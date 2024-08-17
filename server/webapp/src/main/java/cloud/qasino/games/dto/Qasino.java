package cloud.qasino.games.dto;

import cloud.qasino.games.dto.model.GameDto;
import cloud.qasino.games.dto.model.InvitationsDto;
import cloud.qasino.games.dto.model.LeagueDto;
import cloud.qasino.games.dto.model.PlayingDto;
import cloud.qasino.games.dto.model.ResultDto;
import cloud.qasino.games.dto.model.VisitorDto;
import cloud.qasino.games.dto.request.CreationDto;
import cloud.qasino.games.dto.request.MessageDto;
import cloud.qasino.games.dto.request.ParamsDto;
import cloud.qasino.games.pattern.statemachine.event.interfaces.AbstractFlowDto;
import cloud.qasino.games.response.NavigationBarItem;
import cloud.qasino.games.response.enums.EnumOverview;
import cloud.qasino.games.response.statistics.Statistic;
import jakarta.validation.Valid;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Qasino extends AbstractFlowDto {

    // @formatter:off
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
