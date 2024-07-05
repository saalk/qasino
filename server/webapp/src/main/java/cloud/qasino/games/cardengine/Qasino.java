package cloud.qasino.games.cardengine;

import cloud.qasino.games.dto.GameDto;
import cloud.qasino.games.dto.LeagueDto;
import cloud.qasino.games.dto.PlayingDto;
import cloud.qasino.games.dto.VisitorDto;
import cloud.qasino.games.dto.request.ParamsDto;
import cloud.qasino.games.dto.request.MessageDto;
import cloud.qasino.games.dto.InvitationsDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Qasino
{
    private MessageDto message;
    private ParamsDto ids;

    private VisitorDto visitor;
    private LeagueDto league;
    private GameDto game;
    private PlayingDto playing;
    private InvitationsDto invitations;
}
