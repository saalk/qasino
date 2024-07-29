package cloud.qasino.games.action.dto.load;

import cloud.qasino.games.action.dto.ActionDto;
import cloud.qasino.games.action.dto.Qasino;
import cloud.qasino.games.exception.MyNPException;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
/**
 * @param actionDto Validate supplied ids and store objects in Dto for
 * 1) getSuppliedVisitor: Visitor
 * 2) getSuppliedGame: QasinoGame, PlayingPlayer, NextPlayingPlayer, QasinoGamePlayers, CardsInTheGameSorted
 * 3) getSuppliedPlayingPlayer: PlayingPlayer (you might be invited to a Game)
 * 4) getSuppliedLeague: League
 *
 * Business rules:
 * BR1) Visitor: there should always be a valid Visitor supplied - except during signon !!
 * BR2) Game: if there is no Game supplied automatically find the last Game the Visitor initiated, if any
 * if there is a Game (with or without ActivePlaying) always try to find the:
 * BR3) - ActivePlaying + PlayingPlayer: if there is none supplied use Player with seat 1
 * BR4) - list of QasinoGamePlayers and list of CardsInTheGameSorted
 * BR5) - NextPlayingPlayer: find Player with seat after PlayingPlayer - can be same as PlayingPlayer
 * BR6) - League for the Game
 * BR7) todo GameInvitations pending, playing, finished
 *
 * @return Result.SUCCESS or FAILURE (404) when not found
 */
public class LoadResultDtoAction extends ActionDto<EventOutput.Result> {

    @Override
    public EventOutput.Result perform(Qasino qasino) {

        boolean isResultFound = refreshOrFindResultsForGame(qasino);
        if (!isResultFound) {
            // TODO make 404
//          return new EventOutput(EventOutput.Result.FAILURE, actionDto.getIds().getSuppliedGameEvent(), actionDto.getIds().getSuppliedPlayEvent());
            throw new MyNPException("39 getResultSupplied", "resultId [" + qasino.getParams().getSuppliedGameId() + "]");
        }
        return EventOutput.Result.SUCCESS;
    }
}
