package cloud.qasino.games.action.dto;

import cloud.qasino.games.database.entity.enums.game.gamestate.GameStateGroup;
import cloud.qasino.games.database.service.GameService;
import cloud.qasino.games.database.service.PlayerService;
import cloud.qasino.games.database.service.PlayingService;
import cloud.qasino.games.database.service.VisitorAndLeaguesService;
import cloud.qasino.games.dto.SeatDto;
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
    @Autowired VisitorAndLeaguesService visitorAndLeaguesService;
    @Autowired GameService gameService;
    @Autowired @Lazy PlayerService playerService;
    @Autowired @Lazy PlayingService playingService;


    public abstract EventOutput.Result perform(Qasino qasino);

    protected boolean findVisitorByUsername(Qasino qasino) {
        if (qasino.getParams().getSuppliedVisitorUsername().isEmpty()) return true; // 200 not logon yet
        qasino.setVisitor(visitorAndLeaguesService.findByUsername(qasino.getParams()));
        if (qasino.getVisitor() == null) return false; // 404 not found
        qasino.getParams().setSuppliedVisitorId(qasino.getVisitor().getVisitorId());
        refreshOrFindLatestGame(qasino);
        refreshOrFindLeagueForLatestGame(qasino);
        return true; // visitor found and id set
    }
    protected boolean refreshVisitor(Qasino qasino) {
        qasino.setVisitor(visitorAndLeaguesService.findOneByVisitorId(qasino.getParams()));
        if (qasino.getVisitor() == null) return false; // 404 not found
        qasino.getParams().setSuppliedVisitorId(qasino.getVisitor().getVisitorId());
        refreshOrFindLatestGame(qasino);
        refreshOrFindLeagueForLatestGame(qasino);
        return true; // 200 visitor found and id set
    }
    protected boolean refreshOrFindLatestGame(Qasino qasino) {
        if (qasino.getParams().getSuppliedGameId() > 0) {
            qasino.setGame(gameService.findOneByGameId(qasino.getParams()));
            if (qasino.getGame() == null) return false; // 404 not found
        } else {
            qasino.setGame(gameService.findLatestGameForVisitorId(qasino.getParams()));
            if (qasino.getGame() == null) return true; // 200 no game yet
            qasino.getParams().setSuppliedGameId(qasino.getGame().getGameId());
        }
        refreshOrFindPlayingForGame(qasino);
        if (qasino.getGame().getGameStateGroup().equals(GameStateGroup.FINISHED)) {
            refreshOrFindResultsForGame(qasino);
        }
        return true; // 200 game found and id set
    }
    protected boolean refreshOrFindPlayingForGame(Qasino qasino) {
        if (qasino.getParams().getSuppliedGameId() > 0) {
            qasino.setPlaying(playingService.findByGameId(qasino.getParams()));
            if (qasino.getPlaying() == null) return false; // 200 no playing yet
            qasino.getParams().setSuppliedPlayingId(qasino.getPlaying().getPlayingId());
            refreshOrFindSeatsForPlaying(qasino);
            return false;
        }
        return true; // 200 playing found and id set
    }
    protected boolean refreshOrFindSeatsForPlaying(Qasino qasino) {
        List<SeatDto> seats = playingService.findByPlayingOrGameId(qasino.getParams());
        if (seats.isEmpty()) {
            return false;
        }
        qasino.getPlaying().setSeats(seats);
        return true; // 200 seat for playing found
    }
    protected boolean refreshOrFindResultsForGame(Qasino qasino) {
        if (qasino.getParams().getSuppliedGameId() > 0) {
            qasino.setResults(playingService.findResultsByGameId(qasino.getParams()));
            return true; // 200 results for playing found and id set
        }
        return false;
    }
    protected boolean refreshOrFindLeagueForLatestGame(Qasino qasino) {
        if (qasino.getParams().getSuppliedLeagueId() > 0) {
            qasino.setLeague(visitorAndLeaguesService.findOneByLeagueId(qasino.getParams()));
            return qasino.getLeague() != null; // 404 not found
        }
        if (qasino.getParams().getSuppliedGameId() > 0 && qasino.getGame().getLeague() != null) {
            qasino.getParams().setSuppliedLeagueId(qasino.getGame().getLeague().getLeagueId());
            qasino.setLeague(visitorAndLeaguesService.findOneByLeagueId(qasino.getParams()));
            return qasino.getLeague() != null; // 200 or 404 not found
        }
        return false; // 404 -> no game yet or latest game has no league
    }
}
