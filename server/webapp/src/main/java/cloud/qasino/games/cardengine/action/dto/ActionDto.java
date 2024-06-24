package cloud.qasino.games.cardengine.action.dto;

import cloud.qasino.games.cardengine.Qasino;
import cloud.qasino.games.cardengine.cardplay.Table;
import cloud.qasino.games.database.entity.Game;
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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.expression.Ids;

import java.util.List;

@Setter
@Getter
public abstract class ActionDto {

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
    private InvitationsDTO invitations = null;
    private LeagueDto league = null;
    private Table table = null;

    // every child of action must "perform" an action
    abstract void perform();

    // every child can always call find visitor or refresh for any ids
    boolean findVisitorByUserName() {
        visitor = visitorAndLeaguesService.findByUsername(this.ids);
        if (visitor==null) return false;
        this.ids.setSuppliedVisitorId(visitor.getVisitorId());
        // find latest game if any
        game = gameService.findLatestGameForVisitorId(this.ids);
        if (game==null) return true;
        // latest game found
        this.ids.setSuppliedGameId(game.getGameId());
        this.ids.setSuppliedLeagueId(game.getLeagueId());
        // find league for game if any
        if (this.ids.getSuppliedLeagueId() > 0 ) refreshLeague();
        return true;
    }
    void refreshVisitor() {
        visitor = visitorAndLeaguesService.findOneByVisitorId(this.ids);
        // refresh game, league and table as they might have visitors refs
        if (this.ids.getSuppliedGameId() > 0 ) refreshGame();
        if (this.ids.getSuppliedLeagueId() > 0 ) refreshLeague();
    }
    void refreshGame() {
        game = gameService.findOneByGameId(this.ids);
        // refresh table as they might have game and visitor refs
    }
    void refreshLeague() {
        league = visitorAndLeaguesService.findOneByLeagueId(this.ids);
    }
}
