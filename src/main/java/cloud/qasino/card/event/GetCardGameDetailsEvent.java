package cloud.qasino.card.event;

import cloud.qasino.card.entity.Casino;
import cloud.qasino.card.entity.Game;
import cloud.qasino.card.event.common.AbstractEvent;
import cloud.qasino.card.event.common.EventOutput;
import cloud.qasino.card.service.ICasinoService;
import cloud.qasino.card.service.IGameService;
import cloud.qasino.card.statemachine.CardGameStateMachine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class GetCardGameDetailsEvent extends AbstractEvent {
	
	// @Resource = javax, @Inject = javax, @Autowire = spring bean factory
	@Autowired
	private IGameService gameService;
	
	@Autowired
	private ICasinoService casinoService;
	
	@Override
	protected EventOutput execution(final Object... eventInput) {
		
		GetCardGameDetailsEventDTO flowDTO = (GetCardGameDetailsEventDTO) eventInput[0];
		EventOutput eventOutput;
		
		String message = String.format("GetCardGameDetailsEvent game to get is: %s", flowDTO.getSuppliedGameId());
		log.info(message);
		
		//GET    api/cardgames/1
		
		//TODO
		//GET    api/cardgames/1/player                    // gives active casino (resource=player)
		//GET    api/cardgames/1/players                   // gives all casinos (resource=players)
		//GET    api/cardgames/1/cards                     // gives all decks (resources=cards)
		//GET    api/cardgames/1/players/2                 // gives a specific casino (resource=players, resourceId=int)
		//GET    api/cardgames/1/players/2/cards           // gives a all hands for a player (resource=players, resourceId=int, extraResource=cards)
		
		// init
		Game gameToGet;
		
		// check path var cardgame/{id}
		String id = flowDTO.getSuppliedGameId();
		try {
			gameToGet = gameService.findOne(Integer.parseInt(id));
			message = String.format("GetCardGameDetailsEvent game find before get in Event: %s", gameToGet);
			log.info(message);
			if (gameToGet == null) {
				eventOutput = new EventOutput(EventOutput.Result.FAILURE, CardGameStateMachine.Trigger.ERROR);
				return eventOutput;
			}
		} catch (Exception e) {
			message = String.format("GetCardGameDetailsEvent game find before get has exception: %s", e);
			log.info(message);
			eventOutput = new EventOutput(EventOutput.Result.FAILURE, CardGameStateMachine.Trigger.ERROR);
			return eventOutput;
		}
		
		flowDTO.setCurrentGame(gameToGet);
		//TODO
		if (flowDTO.getSuppliedResourceId() == null || flowDTO.getSuppliedResourceId().isEmpty()) {
			eventOutput = new EventOutput(EventOutput.Result.SUCCESS);
			message = String.format("GetCardGameDetailsEvent cardgame id but no resource after cardgame id present");
			log.info(message);
			return eventOutput;
		}
		
		// check path var players/{id} (=players for a game = casinos)
		String casinoId = flowDTO.getSuppliedResourceId();
		List<Casino> casinos; //TODO should be ordered with a set
		try {
			casinos = casinoService.findAllWhere("game", id);
			if (casinos == null) {
				eventOutput = new EventOutput(EventOutput.Result.FAILURE, flowDTO.getSuppliedTrigger());
				return eventOutput;
			}
		} catch (Exception e) {
			eventOutput = new EventOutput(EventOutput.Result.FAILURE, flowDTO.getSuppliedTrigger());
			return eventOutput;
		}
		
		flowDTO.setCurrentCasinos(casinos);
		
		
		eventOutput = new EventOutput(EventOutput.Result.SUCCESS);
		message = String.format("GetCardGameDetailsEvent do no transition");
		log.info(message);
		
		return eventOutput;
	}
	
	public interface GetCardGameDetailsEventDTO {
		
		// all game fields
		String getSuppliedGameId();
		void setCurrentGame(Game game);
		Game getCurrentGame();
		
		// rest
		void setCurrentCasinos(List<Casino> casinos);
		String getSuppliedResourceId();
		
		CardGameStateMachine.Trigger getSuppliedTrigger();
	}
}
