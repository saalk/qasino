package cloud.qasino.games.cardengine;

import cloud.qasino.games.dto.GameDto;
import cloud.qasino.games.dto.LeagueDto;
import cloud.qasino.games.dto.VisitorDto;
import cloud.qasino.games.dto.request.ParamsDto;
import cloud.qasino.games.dto.request.MessageDto;
import cloud.qasino.games.dto.InvitationsDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Qasino
{
    private MessageDto message;
    private ParamsDto ids;
    private VisitorDto visitor;
    private GameDto game;
    private InvitationsDTO invitations;
    private LeagueDto league;
}
