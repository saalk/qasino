package nl.knikit.card.event;

import lombok.extern.slf4j.Slf4j;
import nl.knikit.card.event.common.AbstractEvent;
import nl.knikit.card.event.common.EventOutput;
import nl.knikit.card.entity.enums.AiLevel;
import nl.knikit.card.entity.enums.Avatar;
import nl.knikit.card.entity.Game;
import nl.knikit.card.entity.Player;
import nl.knikit.card.statemachine.CardGameStateMachine;
import nl.knikit.card.service.IPlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CreatePlayerEvent extends AbstractEvent {
	
	// @Resource = javax, @Inject = javax, @Autowire = spring bean factory
	@Autowired
	private IPlayerService playerService;
	
	@Override
	protected EventOutput execution(final Object... eventInput) {
		
		CreatePlayerEventDTO flowDTO = (CreatePlayerEventDTO) eventInput[0];
		EventOutput eventOutput;
		
		// get the game and update the gametype and ante
		Player createdPlayer;
		try {
			createdPlayer = playerService.create(makeConsistentPlayer(flowDTO));
			if (createdPlayer == null) {
				eventOutput = new EventOutput(EventOutput.Result.FAILURE, CardGameStateMachine.Trigger.ERROR);
				return eventOutput;
			}
		} catch (Exception e) {
			eventOutput = new EventOutput(EventOutput.Result.FAILURE, CardGameStateMachine.Trigger.ERROR);
			return eventOutput;
		}
		
		// pass result back to flowDTO
		flowDTO.setCurrentPlayer(createdPlayer);
		flowDTO.setSuppliedPlayerId(String.valueOf(createdPlayer.getPlayerId()));
		
		
		// never do a transition, this is no key event
		eventOutput = new EventOutput(EventOutput.Result.SUCCESS);
		String message = String.format("CreatePlayerEvent never does no transition");
		log.info(message);
		
		return eventOutput;
	}
	
	public interface CreatePlayerEventDTO {
		
		// all game fields
		String getSuppliedGameId();
		void setCurrentGame(Game game);
		Game getCurrentGame();
		
		// rest
		void setCurrentPlayer(Player player);
		void setSuppliedPlayerId(String playerId);
		String getSuppliedHumanOrAi();
		String getSuppliedAlias();
		Avatar getSuppliedAvatar();
		String getSuppliedSecuredLoan();
		AiLevel getSuppliedAiLevel();
		
		CardGameStateMachine.Trigger getSuppliedTrigger();
	}
	
	private Player makeConsistentPlayer(CreatePlayerEventDTO flowDTO) {
		
		// set defaults for human or aline
		Player player = new Player();
		if (flowDTO.getSuppliedHumanOrAi().equals("human")) {
			player.setAiLevel(AiLevel.HUMAN);
			player.setHuman(true);
			if (flowDTO.getSuppliedAlias() == null || flowDTO.getSuppliedAlias().isEmpty()) {
				player.setAlias("Event Stranger");
			} else {
				player.setAlias(flowDTO.getSuppliedAlias());
			}
		} else {
			player.setHuman(false);
			if (flowDTO.getSuppliedAiLevel().equals(AiLevel.HUMAN)) {
				player.setAiLevel(AiLevel.MEDIUM);
			} else {
				player.setAiLevel(flowDTO.getSuppliedAiLevel());
			}
			if (flowDTO.getSuppliedAlias() == null || flowDTO.getSuppliedAlias().isEmpty()) {
				player.setAlias("Event Alien");
			} else {
				player.setAlias(flowDTO.getSuppliedAlias());
			}
		}
		
		if (flowDTO.getSuppliedAvatar() != null) {
			player.setAvatar(flowDTO.getSuppliedAvatar());
		}
		
		if (flowDTO.getSuppliedSecuredLoan() != null && !flowDTO.getSuppliedSecuredLoan().equals("null") && !flowDTO.getSuppliedSecuredLoan().isEmpty()) {
			player.setSecuredLoan(Integer.parseInt(flowDTO.getSuppliedSecuredLoan()));
			player.setCubits(Integer.parseInt(flowDTO.getSuppliedSecuredLoan()));
		}
		return player;
	}
}
