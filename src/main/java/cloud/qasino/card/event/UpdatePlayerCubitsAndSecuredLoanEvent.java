package cloud.qasino.card.event;

import cloud.qasino.card.entity.Game;
import cloud.qasino.card.entity.Player;
import cloud.qasino.card.event.common.AbstractEvent;
import cloud.qasino.card.event.common.EventOutput;
import cloud.qasino.card.service.IPlayerService;
import cloud.qasino.card.statemachine.CardGameStateMachine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UpdatePlayerCubitsAndSecuredLoanEvent extends AbstractEvent {
	
	// @Resource = javax, @Inject = javax, @Autowire = spring bean factory
	@Autowired
	private IPlayerService playerService;
	
	@Override
	protected EventOutput execution(final Object... eventInput) {
		
		UpdatePlayerCubitsAndSecuredLoanEventDTO flowDTO = (UpdatePlayerCubitsAndSecuredLoanEventDTO) eventInput[0];
		EventOutput eventOutput;
		String message;
		
		// INIT
		Player playerToUpdate = flowDTO.getCurrentPlayer();
		Player updatedPlayer;
		
		// UPDATE
		playerToUpdate = makeConsistentCubitsAndSecuredLoan(flowDTO, playerToUpdate);
		try {
			updatedPlayer = playerService.update(playerToUpdate);
			if (updatedPlayer == null) {
				eventOutput = new EventOutput(EventOutput.Result.FAILURE, CardGameStateMachine.Trigger.ERROR);
				return eventOutput;
			}
		} catch (Exception e) {
			eventOutput = new EventOutput(EventOutput.Result.FAILURE, CardGameStateMachine.Trigger.ERROR);
			return eventOutput;
		}
		flowDTO.setCurrentPlayer(updatedPlayer);
		
		// never do a transition, this is no key event
		eventOutput = new EventOutput(EventOutput.Result.SUCCESS);
		return eventOutput;
	}
	
	public interface UpdatePlayerCubitsAndSecuredLoanEventDTO {
		
		// all game fields
		String getSuppliedGameId();
		
		void setCurrentGame(Game game);
		
		Game getCurrentGame();
		
		Player getCurrentPlayer();
		
		// rest
		
		void setCurrentPlayer(Player player);
		
		String getSuppliedSecuredLoan();
		
		int getNewCubits();
		
		CardGameStateMachine.Trigger getSuppliedTrigger();
		
	}
	
	private Player makeConsistentCubitsAndSecuredLoan(UpdatePlayerCubitsAndSecuredLoanEventDTO flowDTO, Player player) {
		
		String message = String.format("UpdatePlayerCubitsAndSecuredLoanEvent makeConsistentCubitsAndSecuredLoan is: %s", player);
		log.info(message);
		
		int oldSecuredLoan = player.getSecuredLoan();
		if (flowDTO.getNewCubits() != 0) {
			
			player.setCubits(player.getCubits() + flowDTO.getNewCubits());
			
			message = String.format("UpdatePlayerCubitsAndSecuredLoanEvent setCubits is: %s", player.getCubits());
			log.info(message);
		}
		if ((flowDTO.getSuppliedSecuredLoan() != null) && (!flowDTO.getSuppliedSecuredLoan().equals("null")) && (!flowDTO.getSuppliedSecuredLoan().isEmpty())) {
			
			player.setSecuredLoan(Integer.parseInt(flowDTO.getSuppliedSecuredLoan()));
			
			message = String.format("UpdatePlayerCubitsAndSecuredLoanEvent setSecuredLoan is: %s", player.getSecuredLoan());
			log.info(message);
			
			int cubitsRaise = player.getSecuredLoan() - oldSecuredLoan;
			player.setCubits(player.getCubits() + cubitsRaise);
			
		}
		return player;
	}
}

