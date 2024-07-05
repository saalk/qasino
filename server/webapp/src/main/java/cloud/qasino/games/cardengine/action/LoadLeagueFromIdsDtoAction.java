package cloud.qasino.games.cardengine.action;

import cloud.qasino.games.cardengine.action.dto.ActionDto;
import cloud.qasino.games.exception.MyNPException;
import cloud.qasino.games.pattern.statemachine.event.EventOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
/**
 * @param actionDto Validate supplied ids and store objects in Dto for
 * 1) getSuppliedVisitor: Visitor
 * 2) getSuppliedGame: QasinoGame, GamingTablePlayer, NextGamingTablePlayer, QasinoGamePlayers, CardsInTheGameSorted
 * 3) getSuppliedGamingTablePlayer: GamingTablePlayer (you might be invited to a Game)
 * 4) getSuppliedLeague: League
 *
 * Business rules:
 * BR1) Visitor: there should always be a valid Visitor supplied - except during signon !!
 * BR2) Game: if there is no Game supplied automatically find the last Game the Visitor initiated, if any
 * if there is a Game (with or without ActiveGamingTable) always try to find the:
 * BR3) - ActiveGamingTable + GamingTablePlayer: if there is none supplied use Player with seat 1
 * BR4) - list of QasinoGamePlayers and list of CardsInTheGameSorted
 * BR5) - NextGamingTablePlayer: find Player with seat after GamingTablePlayer - can be same as GamingTablePlayer
 * BR6) - League for the Game
 * BR7) todo GameInvitations pending, playing, finished
 *
 * @return Result.SUCCESS or FAILURE (404) when not found
 */
public class LoadLeagueFromIdsDtoAction extends ActionDto<EventOutput.Result> {

    @Override
    public EventOutput.Result perform() {

        boolean isLeagueFound = refreshOrFindLeagueForLatestGame();
        if (!isLeagueFound) {
            // TODO make 404
//          return new EventOutput(EventOutput.Result.FAILURE, actionDto.getIds().getSuppliedGameEvent(), actionDto.getIds().getSuppliedPlayEvent());
            throw new MyNPException("69 getVisitorSupplied", "visitorId [" + super.getIds().getSuppliedVisitorId() + "]");
        }
        return EventOutput.Result.SUCCESS;
    }
}
