package nl.knikit.card.event;

import lombok.extern.slf4j.Slf4j;
import nl.knikit.card.entity.Casino;
import nl.knikit.card.entity.Game;
import nl.knikit.card.entity.Player;
import nl.knikit.card.entity.enums.AiLevel;
import nl.knikit.card.entity.enums.Avatar;
import nl.knikit.card.event.common.AbstractEvent;
import nl.knikit.card.event.common.EventOutput;
import nl.knikit.card.statemachine.CardGameStateMachine;
import nl.knikit.card.service.ICasinoService;
import nl.knikit.card.service.IPlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UpdatePlayerForCasinoDetailsEvent extends AbstractEvent {
	
	// @Resource = javax, @Inject = javax, @Autowire = spring bean factory
	@Autowired
	private IPlayerService playerService;
	
	@Autowired
	private ICasinoService casinoService;
	
	@Override
	protected EventOutput execution(final Object... eventInput) {
		
		UpdatePlayerForCasinoDetailsEventDTO flowDTO = (UpdatePlayerForCasinoDetailsEventDTO) eventInput[0];
		EventOutput eventOutput;
		
		
		// get the player and update the playertype and ante
		// init
		Casino casinoToCheck;
		
		Player playerToUpdate;
		Player updatedPlayer;
		
		String message = String.format("UpdatePlayerForCasinoDetailsEvent getSuppliedCasinoId is: %s", flowDTO.getSuppliedCasinoId());
		log.info(message);
		
		// find casino to update
		String casinoId = flowDTO.getSuppliedCasinoId();
		try {
			casinoToCheck = casinoService.findOne(Integer.parseInt(casinoId));
			if (casinoToCheck == null) {
				eventOutput = new EventOutput(EventOutput.Result.FAILURE, flowDTO.getSuppliedTrigger());
				return eventOutput;
			}
		} catch (Exception e) {
			eventOutput = new EventOutput(EventOutput.Result.FAILURE, flowDTO.getSuppliedTrigger());
			return eventOutput;
		}
		
		message = String.format("UpdatePlayerForCasinoDetailsEvent casinoToCheck.getPlayer().getPlayerId() is: %s", casinoToCheck.getPlayer().getPlayerId());
		log.info(message);
		
		// check path var player/{id}
		String id = String.valueOf(casinoToCheck.getPlayer().getPlayerId());
		try {
			playerToUpdate = playerService.findOne(Integer.parseInt(id));
			if (playerToUpdate == null) {
				eventOutput = new EventOutput(EventOutput.Result.FAILURE, flowDTO.getSuppliedTrigger());
				return eventOutput;
			}
		} catch (Exception e) {
			eventOutput = new EventOutput(EventOutput.Result.FAILURE, flowDTO.getSuppliedTrigger());
			return eventOutput;
		}
		
		// do the update
		try {
			updatedPlayer = playerService.update(makeConsistentPlayer(flowDTO, playerToUpdate));
			if (updatedPlayer == null) {
				eventOutput = new EventOutput(EventOutput.Result.FAILURE, CardGameStateMachine.Trigger.ERROR);
				return eventOutput;
			}
		} catch (Exception e) {
			eventOutput = new EventOutput(EventOutput.Result.FAILURE, CardGameStateMachine.Trigger.ERROR);
			return eventOutput;
		}
		
		// store player and id
		flowDTO.setCurrentPlayer(updatedPlayer);
		flowDTO.setSuppliedPlayerId(String.valueOf(updatedPlayer.getPlayerId()));
		
		
		message = String.format("UpdatePlayerForCasinoDetailsEvent getSuppliedPlayerId is: %s", flowDTO.getSuppliedPlayerId());
		log.info(message);
		
		// never do a transition, this is no key event
		eventOutput = new EventOutput(EventOutput.Result.SUCCESS);
		message = String.format("UpdatePlayerForCasinoDetailsEvent never does no transition");
		log.info(message);
		
		return eventOutput;
	}
	
	public interface UpdatePlayerForCasinoDetailsEventDTO {
		
		// all game fields
		String getSuppliedGameId();
		
		void setCurrentGame(Game game);
		
		Game getCurrentGame();
		
		// rest
		
		void setCurrentPlayer(Player player);
		
		void setSuppliedPlayerId(String playerId);
		
		String getSuppliedPlayerId();
		
		String getSuppliedCasinoId();
		
		String getSuppliedHumanOrAi();
		
		String getSuppliedAlias();
		
		Avatar getSuppliedAvatar();
		
		AiLevel getSuppliedAiLevel();
		
		CardGameStateMachine.Trigger getSuppliedTrigger();
		
	}
	
	private Player makeConsistentPlayer(UpdatePlayerForCasinoDetailsEventDTO flowDTO, Player playerToUpdate) {
		
		// set defaults for human or aline
		if (playerToUpdate.getHuman()) {
			playerToUpdate.setAiLevel(AiLevel.HUMAN);
			if (flowDTO.getSuppliedAlias() != null && !flowDTO.getSuppliedAlias().isEmpty()) {
				playerToUpdate.setAlias(flowDTO.getSuppliedAlias());
			}
		} else {
			if (flowDTO.getSuppliedAiLevel().equals(AiLevel.HUMAN)) {
				playerToUpdate.setAiLevel(AiLevel.MEDIUM);
			} else {
				playerToUpdate.setAiLevel(flowDTO.getSuppliedAiLevel());
			}
			if (flowDTO.getSuppliedAlias() != null && !flowDTO.getSuppliedAlias().isEmpty()) {
				playerToUpdate.setAlias(flowDTO.getSuppliedAlias());
			}
		}
		if (flowDTO.getSuppliedAvatar() != null) {
			playerToUpdate.setAvatar(flowDTO.getSuppliedAvatar());
		}
		
		return playerToUpdate;
	}
}

