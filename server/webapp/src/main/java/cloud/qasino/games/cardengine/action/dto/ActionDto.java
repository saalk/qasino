package cloud.qasino.games.cardengine.action.dto;

import cloud.qasino.games.cardengine.cardplay.Table;
import cloud.qasino.games.database.entity.Turn;
import cloud.qasino.games.database.service.GameService;
import cloud.qasino.games.database.service.PlayerService;
import cloud.qasino.games.database.service.TurnAndCardMoveService;
import cloud.qasino.games.database.service.VisitorAndLeaguesService;
import cloud.qasino.games.dto.GameDto;
import cloud.qasino.games.dto.InvitationsDTO;
import cloud.qasino.games.dto.LeagueDto;
import cloud.qasino.games.dto.VisitorDto;
import cloud.qasino.games.dto.request.CreationDto;
import cloud.qasino.games.dto.request.IdsDto;
import cloud.qasino.games.dto.request.MessageDto;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.beans.factory.annotation.Autowired;

@Setter
@Getter
// Implement OUTPUT with EventOutput.Result or boolean TRUE/FALSE
public abstract class ActionDto<OUTPUT> {

    // @formatter:off
    // we do not always need every services so its lazy loading here and at the service
    @Autowired @Lazy VisitorAndLeaguesService visitorAndLeaguesService;
    @Autowired @Lazy GameService gameService;
    @Autowired @Lazy PlayerService playerService;
    @Autowired @Lazy TurnAndCardMoveService turnAndCardMoveService;

    private MessageDto message = new MessageDto();
    private IdsDto ids = new IdsDto();
    private CreationDto creation = new CreationDto();

    private VisitorDto visitor = null;
    private GameDto game = null;
    private LeagueDto league = null;

    private InvitationsDTO invitations = null;
    private Table table = null;
    // @formatter:on

    public abstract EventOutput.Result perform();

    protected boolean findVisitorByUserName() {
        visitor = visitorAndLeaguesService.findByUsername(this.ids);
        if (visitor == null) return false; // 404 not found
        this.ids.setSuppliedVisitorId(visitor.getVisitorId());
        refreshOrFindLatestGame();
        refreshOrFindLeagueForLatestGame();
        return true;
    }

    protected boolean refreshVisitor() {
        visitor = visitorAndLeaguesService.findOneByVisitorId(this.ids);
        if (visitor == null) return false; // 404 not found
        refreshOrFindLatestGame();
        refreshOrFindLeagueForLatestGame();
        return true;
    }

    protected boolean refreshOrFindLatestGame() {
        if (this.ids.getSuppliedGameId() > 0) {
            game = gameService.findOneByGameId(this.ids);
            if (game == null) return false; // 404 not found
        } else {
            game = gameService.findLatestGameForVisitorId(this.ids);
            if (game == null) return true; // 200 no game yet
        }
        // 200 game found
        this.ids.setSuppliedGameId(game.getGameId());
        return true;
    }

    protected Turn refreshOrFindTurnForGame() {
        if (this.ids.getSuppliedGameId() > 0) {
            return turnAndCardMoveService.findByGameId(this.ids);
        }
        return null;
    }

    protected boolean refreshOrFindLeagueForLatestGame() {
        if (this.ids.getSuppliedLeagueId() > 0) {
            league = visitorAndLeaguesService.findOneByLeagueId(this.ids);
            return league != null; // 200 or 404 not found
        }
        if (this.ids.getSuppliedGameId() > 0 && game.getLeagueId() > 0) {
            this.ids.setSuppliedLeagueId(game.getLeagueId());
            league = visitorAndLeaguesService.findOneByLeagueId(this.ids);
            return league != null; // 200 or 404 not found
        }
        return true; // 200 -> no game yet or latest game has no league
    }
}
