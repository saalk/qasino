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

@Component
@Slf4j
public class UpdateCasinoForTurnAndBetEvent extends AbstractEvent {
	
	// @Resource = javax, @Inject = javax, @Autowire = spring bean factory
	@Autowired
	private ICasinoService casinoService;
	
	@Override
	protected EventOutput execution(final Object... eventInput) {
		
		UpdateCasinoForTurnAndBetEventDTO flowDTO = (UpdateCasinoForTurnAndBetEventDTO) eventInput[0];
		EventOutput eventOutput;
		String message;
		
		// INIT
		Casino casinoToUpdate = flowDTO.getCurrentCasino();
		Casino casinoUpdated;
		
		if (flowDTO.getNewCurrentTurn() != 0) {
			casinoToUpdate.setActiveTurn((flowDTO.getNewCurrentTurn()));
		}
		if (flowDTO.getNewBet() != 0) {
			casinoToUpdate.setBet((flowDTO.getNewBet()));
		}
		
		message = String.format("UpdateCasinoForTurnAndBetEvent setBet is going to be: ", casinoToUpdate.getBet());
		log.info(message);
		
		// UPDATE
		try {
			casinoUpdated = casinoService.update(casinoToUpdate);
		} catch (Exception e) {
			eventOutput = new EventOutput(EventOutput.Result.FAILURE, CardGameStateMachine.Trigger.ERROR);
			return eventOutput;
		}
		flowDTO.setCurrentCasino(casinoUpdated);
		flowDTO.setSuppliedPlayerId(String.valueOf(casinoUpdated.getPlayer().getPlayerId()));
		
		eventOutput = new EventOutput(EventOutput.Result.SUCCESS);
		message = String.format("UpdateCasinoForTurnAndBetEvent do no transition");
		log.info(message);
		return eventOutput;
	}
	
	public interface UpdateCasinoForTurnAndBetEventDTO {
		
		// all game fields
		String getSuppliedGameId();
		
		void setCurrentGame(Game game);
		
		Game getCurrentGame();
		
		Casino getCurrentCasino();
		
		// rest
		void setCurrentCasino(Casino casino);
		
		void setSuppliedPlayerId(String playerId);
		
		int getNewCurrentTurn();
		
		int getNewBet();
		
		CardGameStateMachine.Trigger getSuppliedTrigger();
		
	}
}
