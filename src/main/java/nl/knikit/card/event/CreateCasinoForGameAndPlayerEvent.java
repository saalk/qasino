package nl.knikit.card.event;

import lombok.extern.slf4j.Slf4j;
import nl.knikit.card.event.common.AbstractEvent;
import nl.knikit.card.event.common.EventOutput;
import nl.knikit.card.entity.Casino;
import nl.knikit.card.entity.Game;
import nl.knikit.card.entity.Player;
import nl.knikit.card.statemachine.CardGameStateMachine;
import nl.knikit.card.service.ICasinoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class CreateCasinoForGameAndPlayerEvent extends AbstractEvent {
	
	// @Resource = javax, @Inject = javax, @Autowire = spring bean factory
//	@Autowired
//	private IGameService gameService;
//
//	@Autowired
//	private IPlayerService playerService;
//
	@Autowired
	private ICasinoService casinoService;
	
//	@Autowired
//	private ModelMapperUtil mapUtil;
	
	@Override
	protected EventOutput execution(final Object... eventInput) {
		
		CreateCasinoForGameAndPlayerEventDTO flowDTO = (CreateCasinoForGameAndPlayerEventDTO) eventInput[0];
		EventOutput eventOutput;
		
		// check the game
		Game gameToCheck = flowDTO.getCurrentGame();
		Player playerToCheck = flowDTO.getCurrentPlayer();
		
		Casino casinoToCreate = new Casino();
		Casino createdCasino;
		
		List<Casino> existingCasinos = flowDTO.getCurrentCasinos();
		
		// do the add
		casinoToCreate.setGame(gameToCheck);
		casinoToCreate.setPlayer(playerToCheck);
		casinoToCreate.setPlayingOrder(existingCasinos.size() + 1);
		casinoToCreate.setBet(0);
		casinoToCreate.setActiveTurn(0);
		
		try {
			createdCasino = casinoService.create(casinoToCreate);
			if (createdCasino == null) {
				eventOutput = new EventOutput(EventOutput.Result.FAILURE, CardGameStateMachine.Trigger.ERROR);
				return eventOutput;
			}
		} catch (Exception e) {
			eventOutput = new EventOutput(EventOutput.Result.FAILURE, CardGameStateMachine.Trigger.ERROR);
			return eventOutput;
		}
		
		// OK, set a trigger for EventOutput to trigger a transition in the state machine
		//flowDTO.setCurrentGame(gameService.findOne(Integer.parseInt(gameId)));
		String message = String.format("CreateCasinoForGameAndPlayerEvent setCurrentGame is: %s", flowDTO.getCurrentGame());
		log.info(message);
		
		flowDTO.setCurrentCasino(createdCasino);
		flowDTO.setSuppliedCasinoId(String.valueOf(createdCasino.getCasinoId()));
		
		
		if (flowDTO.getSuppliedTrigger() == CardGameStateMachine.Trigger.POST_INIT_HUMAN ||
				    flowDTO.getSuppliedTrigger() == CardGameStateMachine.Trigger.POST_SETUP_HUMAN ||
				    flowDTO.getSuppliedTrigger() == CardGameStateMachine.Trigger.POST_HUMAN) {
			// key event so do a transition
			eventOutput = new EventOutput(EventOutput.Result.SUCCESS, flowDTO.getSuppliedTrigger());
			message = String.format("CreateCasinoForGameAndPlayerEvent do a transition with trigger is: %s", flowDTO.getSuppliedTrigger());
			log.info(message);
		} else {
			eventOutput = new EventOutput(EventOutput.Result.SUCCESS);
			message = String.format("CreateCasinoForGameAndPlayerEvent do no transition");
			log.info(message);
		}
		
		return eventOutput;
	}
	
	public interface CreateCasinoForGameAndPlayerEventDTO {
		
		// all game fields
		String getSuppliedGameId();
		
		void setCurrentGame(Game game);
		
		Game getCurrentGame();
		
		Player getCurrentPlayer();
		
		// rest
		String getSuppliedPlayerId();
		
		void setCurrentCasino(Casino casino);
		
		List<Casino> getCurrentCasinos();
		
		void setSuppliedCasinoId(String casinoId);
		
		CardGameStateMachine.Trigger getSuppliedTrigger();
		
	}
}
