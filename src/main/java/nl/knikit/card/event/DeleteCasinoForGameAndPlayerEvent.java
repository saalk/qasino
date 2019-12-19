package nl.knikit.card.event;

import lombok.extern.slf4j.Slf4j;
import nl.knikit.card.event.common.AbstractEvent;
import nl.knikit.card.event.common.EventOutput;
import nl.knikit.card.entity.Casino;
import nl.knikit.card.entity.Game;
import nl.knikit.card.statemachine.CardGameStateMachine;
import nl.knikit.card.service.ICasinoService;
import nl.knikit.card.service.IGameService;
import nl.knikit.card.service.IPlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class DeleteCasinoForGameAndPlayerEvent extends AbstractEvent {
	
	// @Resource = javax, @Inject = javax, @Autowire = spring bean factory
	@Autowired
	private IGameService gameService;
	
	@Autowired
	private IPlayerService playerService;
	
	@Autowired
	private ICasinoService casinoService;
	
	@Override
	protected EventOutput execution(final Object... eventInput) {
		
		DeleteCasinoForGameAndPlayerEventDTO flowDTO = (DeleteCasinoForGameAndPlayerEventDTO) eventInput[0];
		EventOutput eventOutput;
		
		// get the game and update the gametype and ante
		Game gameToCheck;
		Casino casinoToDelete;
		Casino otherCasinoToUpdate;
		
		String message = String.format("DeleteCasinoForGameAndPlayerEvent getSuppliedGameId is: %s", flowDTO.getSuppliedGameId());
		log.info(message);
		
		// always check the game
		String gameId = flowDTO.getSuppliedGameId();
		try {
			gameToCheck = gameService.findOne(Integer.parseInt(gameId));
			if (gameToCheck == null) {
				eventOutput = new EventOutput(EventOutput.Result.FAILURE, flowDTO.getSuppliedTrigger());
				return eventOutput;
			}
		} catch (Exception e) {
			eventOutput = new EventOutput(EventOutput.Result.FAILURE, flowDTO.getSuppliedTrigger());
			return eventOutput;
		}
		
		message = String.format("DeleteCasinoForGameAndPlayerEvent getSuppliedCasinoId is: %s", flowDTO.getSuppliedCasinoId());
		log.info(message);
		
		// find casino to delete
		String casinoId = flowDTO.getSuppliedCasinoId();
		try {
			casinoToDelete = casinoService.findOne(Integer.parseInt(casinoId));
			if (casinoToDelete == null) {
				eventOutput = new EventOutput(EventOutput.Result.FAILURE, flowDTO.getSuppliedTrigger());
				return eventOutput;
			}
		} catch (Exception e) {
			eventOutput = new EventOutput(EventOutput.Result.FAILURE, flowDTO.getSuppliedTrigger());
			return eventOutput;
		}
		
		// do the delete
		try {
			casinoService.deleteOne(casinoToDelete);
		} catch (Exception e) {
			eventOutput = new EventOutput(EventOutput.Result.FAILURE, flowDTO.getSuppliedTrigger());
			return eventOutput;
		}
		
		// sort other casinos on playing order
		List<Casino> allCasinosForAGame = gameToCheck.getCasinos();
		Map<Integer, Casino> casinosSorted = new HashMap<>(); // is sorted key automatically
		for (Casino casino : allCasinosForAGame) {
			casinosSorted.put(casino.getPlayingOrder(), casino);
		}
		casinosSorted.remove(casinoToDelete.getPlayingOrder());
		
		// do the update
		int order = 1;
		int countHumans = 0;
		try {
			// change playing order on the others
			for (Casino casino : casinosSorted.values()) {
				casino.setPlayingOrder(order++);
				casinoService.update(casino);
				if (casino.getPlayer().getHuman()) {
					countHumans++;
				}
				
				message = String.format("DeleteCasinoForGameAndPlayerEvent updateOther is: %s", casino);
				log.info(message);
				
			}
		} catch (Exception e) {
			eventOutput = new EventOutput(EventOutput.Result.FAILURE, flowDTO.getSuppliedTrigger());
			return eventOutput;
		}
		
		// OK, set a trigger for EventOutput to trigger a transition in the state machine
		flowDTO.setCurrentGame(gameService.findOne(Integer.parseInt(gameId)));
		message = String.format("DeleteCasinoForGameAndPlayerEvent setCurrentGame is: %s", flowDTO.getCurrentGame());
		log.info(message);
		
		flowDTO.setCurrentCasino(null);
		
		if ((flowDTO.getSuppliedTrigger() == CardGameStateMachine.Trigger.DELETE_SETUP_HUMAN) &&
				    (countHumans == 0)){
			// key event so do a transition but only when no human left human
			eventOutput = new EventOutput(EventOutput.Result.SUCCESS, flowDTO.getSuppliedTrigger());
			message = String.format("DeleteCasinoForGameAndPlayerEvent do a transition with trigger is: %s", flowDTO.getSuppliedTrigger());
			log.info(message);
		} else {
			eventOutput = new EventOutput(EventOutput.Result.SUCCESS);
			message = String.format("DeleteCasinoForGameAndPlayerEvent do no transition");
			log.info(message);
		}
		
		return eventOutput;
	}
	
	public interface DeleteCasinoForGameAndPlayerEventDTO {
		
		// all game fields
		String getSuppliedGameId();
		
		void setCurrentGame(Game game);
		
		Game getCurrentGame();
		
		// rest
		String getSuppliedCasinoId();
		
		void setCurrentCasino(Casino casino);
		
		CardGameStateMachine.Trigger getSuppliedTrigger();
		
	}
}
