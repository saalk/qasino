package nl.knikit.card.event;

import lombok.extern.slf4j.Slf4j;
import nl.knikit.card.event.common.AbstractEvent;
import nl.knikit.card.event.common.EventOutput;
import nl.knikit.card.entity.Casino;
import nl.knikit.card.entity.Game;
import nl.knikit.card.statemachine.CardGameStateMachine;
import nl.knikit.card.service.ICasinoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class UpdateCasinoForPlayingOrderEvent extends AbstractEvent {
	
	// @Resource = javax, @Inject = javax, @Autowire = spring bean factory
	@Autowired
	private ICasinoService casinoService;
	
	@Override
	protected EventOutput execution(final Object... eventInput) {
		
		UpdateCasinoForPlayingOrderEventDTO flowDTO = (UpdateCasinoForPlayingOrderEventDTO) eventInput[0];
		EventOutput eventOutput;
		String message;
		
		
		// INIT
		Game gameToCheck = flowDTO.getCurrentGame();
		Casino casinoToUpdate = flowDTO.getCurrentCasino();
		Casino casinoUpdated = null;
		Casino otherCasinoToUpdate;
		
		// check if playing order is up (-1) or down (+1)
		boolean playingOrderChanged = false;
		boolean moveTowardsFirst = false;
		boolean moveTowardsLast = false;
		
		if (flowDTO.getSuppliedPlayingOrder() == null || flowDTO.getSuppliedPlayingOrder().equals("null") || flowDTO.getSuppliedPlayingOrder().isEmpty()) {
			eventOutput = new EventOutput(EventOutput.Result.SUCCESS);
			message = String.format("UpdateCasinoForPlayingOrderEvent no getSuppliedPlayingOrder supplied");
			log.info(message);
			return eventOutput;
		} else if (flowDTO.getSuppliedPlayingOrder().equals("-1")) {
			moveTowardsFirst = true;
			playingOrderChanged = true;
		} else if ((flowDTO.getSuppliedPlayingOrder().equals("+1"))) {
			moveTowardsLast = true;
			playingOrderChanged = true;
		} else {
			playingOrderChanged = false;
			moveTowardsFirst = false;
			moveTowardsLast = false;
		}
		// sort casinos on playing order
		List<Casino> allCasinosForAGame = gameToCheck.getCasinos();
		Map<Integer, Casino> casinosSorted = new HashMap<>(); // is sorted key automatically
		for (Casino casino : allCasinosForAGame) {
			casinosSorted.put(casino.getPlayingOrder(), casino);
		}
		
		// see if the change can be done
		if ((casinoToUpdate.getPlayingOrder() == 1) && (playingOrderChanged) && (moveTowardsFirst)) {
			playingOrderChanged = false;
		} else if ((casinoToUpdate.getPlayingOrder() == allCasinosForAGame.size()) && (playingOrderChanged) && (moveTowardsLast)) {
			playingOrderChanged = false;
		}
		
		if (playingOrderChanged) {
			// do the switch
			try {
				Integer oldPlayingOrder = casinoToUpdate.getPlayingOrder();
				
				// update the current
				Integer newPlayingOrder = moveTowardsFirst ? (casinoToUpdate.getPlayingOrder() - 1) : (casinoToUpdate.getPlayingOrder() + 1);
				casinoToUpdate.setPlayingOrder(newPlayingOrder);
				casinoUpdated = casinoService.update(casinoToUpdate);
				
				// find the other that is currently on the newPlayingOrder
				otherCasinoToUpdate = casinosSorted.get(newPlayingOrder);
				
				otherCasinoToUpdate.setPlayingOrder(oldPlayingOrder);
				casinoService.update(otherCasinoToUpdate);
				
			} catch (Exception e) {
				eventOutput = new EventOutput(EventOutput.Result.FAILURE, flowDTO.getSuppliedTrigger());
				return eventOutput;
			}
		}
		
		message = String.format("UpdateCasinoForPlayingOrderEvent setCurrentGame is: %s", flowDTO.getCurrentGame());
		log.info(message);
		
		flowDTO.setCurrentCasino(casinoUpdated);
		
		if (flowDTO.getSuppliedTrigger() == CardGameStateMachine.Trigger.DELETE_SETUP_HUMAN) {
			// key event so do a transition but only when deleting a human
			eventOutput = new EventOutput(EventOutput.Result.SUCCESS, flowDTO.getSuppliedTrigger());
			message = String.format("UpdateCasinoForPlayingOrderEvent do a transition with trigger is: %s", flowDTO.getSuppliedTrigger());
			log.info(message);
		} else {
			eventOutput = new EventOutput(EventOutput.Result.SUCCESS);
			message = String.format("UpdateCasinoForPlayingOrderEvent do no transition");
			log.info(message);
		}
		return eventOutput;
	}
	
	public interface UpdateCasinoForPlayingOrderEventDTO {
		
		// all game fields
		String getSuppliedGameId();
		
		void setCurrentGame(Game game);
		
		Game getCurrentGame();
		Casino getCurrentCasino();
		
		// rest
		String getSuppliedCasinoId();
		
		void setCurrentCasino(Casino casino);
		
		String getSuppliedPlayingOrder();
		
		void setSuppliedPlayerId(String playerId);
		
		CardGameStateMachine.Trigger getSuppliedTrigger();
		
	}
}
