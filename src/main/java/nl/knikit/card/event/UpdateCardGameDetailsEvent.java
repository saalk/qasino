package nl.knikit.card.event;

import lombok.extern.slf4j.Slf4j;
import nl.knikit.card.event.common.AbstractEvent;
import nl.knikit.card.event.common.EventOutput;
import nl.knikit.card.entity.Game;
import nl.knikit.card.entity.enums.GameType;
import nl.knikit.card.statemachine.CardGameStateMachine;
import nl.knikit.card.service.IGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UpdateCardGameDetailsEvent extends AbstractEvent {
	
	// @Resource = javax, @Inject = javax, @Autowire = spring bean factory
	@Autowired
	private IGameService gameService;
	
	@Override
	protected EventOutput execution(final Object... eventInput) {
		
		UpdateCardGameDetailsEventDTO flowDTO = (UpdateCardGameDetailsEventDTO) eventInput[0];
		EventOutput eventOutput;
		
		String message = String.format("UpdateCardGameDetailsEvent game to update is: %s", flowDTO.getSuppliedGameId());
		log.info(message);
		
		// get the game and update the gametype and ante
		// init
		Game gameToUpdate = flowDTO.getCurrentGame();
		Game updatedGame;
		
		// find out what to update
		if (flowDTO.getSuppliedGameType() != null) {
			gameToUpdate.setGameType(flowDTO.getSuppliedGameType());
		}
		if (flowDTO.getSuppliedGameVariant() != null) {
			gameToUpdate.setGameVariant(flowDTO.getSuppliedGameVariant());
		}
		if (flowDTO.getSuppliedAnte() != null) {
			gameToUpdate.setAnte(Integer.parseInt(flowDTO.getSuppliedAnte()));
		}
		
		message = String.format("UpdateCardGameDetailsEvent getSuppliedActiveCasino is: %s", flowDTO.getNewActiveCasino());
		log.info(message);
		
		if (flowDTO.getNewActiveCasino() != 0) {
			gameToUpdate.setActiveCasino(flowDTO.getNewActiveCasino());
		}
		if (flowDTO.getNewCurrentRound() != 0) {
			gameToUpdate.setCurrentRound(flowDTO.getNewCurrentRound());
		}
		
		// do the update
		try {
			updatedGame = gameService.update(gameToUpdate);
			if (updatedGame == null) {
				eventOutput = new EventOutput(EventOutput.Result.FAILURE, CardGameStateMachine.Trigger.ERROR);
				return eventOutput;
			}
		} catch (Exception e) {
			eventOutput = new EventOutput(EventOutput.Result.FAILURE, CardGameStateMachine.Trigger.ERROR);
			return eventOutput;
		}
		
		// OK, set a trigger for EventOutput to trigger a transition in the state machine
		flowDTO.setCurrentGame(updatedGame);
		flowDTO.setSuppliedGameId(String.valueOf(updatedGame.getGameId()));
		
		if (flowDTO.getSuppliedTrigger() == CardGameStateMachine.Trigger.POST_INIT ||
				    flowDTO.getSuppliedTrigger() == CardGameStateMachine.Trigger.PUT_PASS_TURN) {
			// key event so do a transition
			eventOutput = new EventOutput(EventOutput.Result.SUCCESS, flowDTO.getSuppliedTrigger());
			message = String.format("UpdateCardGameDetailsEvent do a transition with trigger is: %s", flowDTO.getSuppliedTrigger());
			log.info(message);
		} else {
			eventOutput = new EventOutput(EventOutput.Result.SUCCESS);
			message = String.format("UpdateCardGameDetailsEvent do no transition");
			log.info(message);
		}
		
		return eventOutput;
	}
	
	public interface UpdateCardGameDetailsEventDTO {
		
		// all game fields
		String getSuppliedGameId();
		
		void setSuppliedGameId(String gameId);
		
		void setCurrentGame(Game game);
		
		Game getCurrentGame();
		
		// rest
		GameType getSuppliedGameType();
		
		String getSuppliedGameVariant();
		
		String getSuppliedAnte();
		
		int getNewCurrentRound();
		
		int getNewActiveCasino();
		
		CardGameStateMachine.Trigger getSuppliedTrigger();
		
	}
}
