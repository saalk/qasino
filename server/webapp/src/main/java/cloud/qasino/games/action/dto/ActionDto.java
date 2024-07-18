package cloud.qasino.games.action.dto;

import cloud.qasino.games.database.entity.enums.game.gamestate.GameStateGroup;
import cloud.qasino.games.database.service.GameService;
import cloud.qasino.games.database.service.PlayerService;
import cloud.qasino.games.database.service.PlayingService;
import cloud.qasino.games.database.service.VisitorAndLeaguesService;
import cloud.qasino.games.dto.GameDto;
import cloud.qasino.games.dto.InvitationsDto;
import cloud.qasino.games.dto.LeagueDto;
import cloud.qasino.games.dto.PlayingDto;
import cloud.qasino.games.dto.ResultDto;
import cloud.qasino.games.dto.SeatDto;
import cloud.qasino.games.dto.VisitorDto;
import cloud.qasino.games.dto.request.CreationDto;
import cloud.qasino.games.dto.request.MessageDto;
import cloud.qasino.games.dto.request.ParamsDto;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Setter
@Getter
// Implement OUTPUT with EventOutput.Result or boolean TRUE/FALSE
public abstract class ActionDto<OUTPUT> {

    // @formatter:off
    // we do not always need every services so its lazy loading here and at the actionDto
    @Autowired @Lazy VisitorAndLeaguesService visitorAndLeaguesService;
    @Autowired @Lazy GameService gameService;
    @Autowired @Lazy PlayerService playerService;
    @Autowired @Lazy PlayingService playingService;

    private MessageDto message = new MessageDto();
    private ParamsDto params = new ParamsDto();
    private CreationDto creation = new CreationDto();

    private VisitorDto visitor = null;
    private LeagueDto league = null;
    private GameDto game = null;
    private PlayingDto playing = null;
    private List<SeatDto> seats = null;
    private List<ResultDto> results = null;
    private InvitationsDto invitations = null;

//    private Table table = null;
    // @formatter:on

    public abstract EventOutput.Result perform(Qasino qasino);

    protected boolean findVisitorByUsername(Qasino qasino) {
        visitor = visitorAndLeaguesService.findByUsername(this.params);
        if (visitor == null) return false; // 404 not found
        this.params.setSuppliedVisitorId(visitor.getVisitorId());
        refreshOrFindLatestGame(qasino);
        refreshOrFindLeagueForLatestGame(qasino);
        return true; // visitor found and id set
    }

    protected boolean refreshVisitor(Qasino qasino) {
        visitor = visitorAndLeaguesService.findOneByVisitorId(this.params);
        if (visitor == null) return false; // 404 not found
        this.params.setSuppliedVisitorId(visitor.getVisitorId());
        refreshOrFindLatestGame(qasino);
        refreshOrFindLeagueForLatestGame(qasino);
        return true; // 200 visitor found and id set
    }

    protected boolean refreshOrFindLatestGame(Qasino qasino) {
        if (this.params.getSuppliedGameId() > 0) {
            game = gameService.findOneByGameId(this.params);
            if (game == null) return false; // 404 not found
        } else {
            game = gameService.findLatestGameForVisitorId(this.params);
            if (game == null) return true; // 200 no game yet
            this.params.setSuppliedGameId(game.getGameId());
        }
        refreshOrFindPlayingForGame(qasino);
        if (game.getGameStateGroup().equals(GameStateGroup.FINISHED)) {
            refreshOrFindResultsForGame(qasino);
        }
        return true; // 200 game found and id set
    }

    protected boolean refreshOrFindPlayingForGame(Qasino qasino) {
        if (this.params.getSuppliedGameId() > 0) {
            playing = playingService.findByGameId(this.params);
            if (playing == null) return false; // 200 no playing yet
            this.params.setSuppliedPlayingId(playing.getPlayingId());
            refreshOrFindSeatsForPlaying(qasino);
            return false;
        }
        return true; // 200 playing found and id set
    }

    protected boolean refreshOrFindSeatsForPlaying(Qasino qasino) {
        seats = playingService.findByPlayingOrGameId(this.params);
        if (seats.isEmpty()) {
            return false;
        }
        playing.setSeats(seats);
        return true; // 200 seat for playing found
    }

    protected boolean refreshOrFindResultsForGame(Qasino qasino) {
        if (this.params.getSuppliedGameId() > 0) {
            results = playingService.findResultsByGameId(this.params);
            return true; // 200 results for playing found and id set
        }
        return false;
    }

    protected boolean refreshOrFindLeagueForLatestGame(Qasino qasino) {
        if (this.params.getSuppliedLeagueId() > 0) {
            league = visitorAndLeaguesService.findOneByLeagueId(this.params);
            return league != null; // 404 not found
        }
        if (this.params.getSuppliedGameId() > 0 && game.getLeague() != null) {
            this.params.setSuppliedLeagueId(game.getLeague().getLeagueId());
            league = visitorAndLeaguesService.findOneByLeagueId(this.params);
            return league != null; // 200 or 404 not found
        }
        return false; // 404 -> no game yet or latest game has no league
    }
}
