package nl.knikit.card.event;

import lombok.extern.slf4j.Slf4j;
import nl.knikit.card.event.common.AbstractEvent;
import nl.knikit.card.event.common.EventOutput;
import nl.knikit.card.entity.Casino;
import nl.knikit.card.entity.Game;
import nl.knikit.card.entity.Player;
import nl.knikit.card.statemachine.CardGameStateMachine;
import nl.knikit.card.service.ICasinoService;
import nl.knikit.card.service.IGameService;
import nl.knikit.card.service.IPlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class DeleteCardGameEvent extends AbstractEvent {
	
	// @Resource = javax, @Inject = javax, @Autowire = spring bean factory
	@Autowired
	private IGameService gameService;
	
	@Autowired
	private IPlayerService playerService;
	
	@Autowired
	private ICasinoService casinoService;
	
	@Override
	protected EventOutput execution(final Object... eventInput) {
		
		DeleteCardGameEventDTO flowDTO = (DeleteCardGameEventDTO) eventInput[0];
		EventOutput eventOutput;
		
		Game gameToDelete;
		try {
			gameToDelete = gameService.findOne(Integer.parseInt(flowDTO.getSuppliedGameId()));
			if (gameToDelete == null) {
				eventOutput = new EventOutput(EventOutput.Result.FAILURE, flowDTO.getSuppliedTrigger());
				return eventOutput;
			}
		} catch (Exception e) {
			eventOutput = new EventOutput(EventOutput.Result.FAILURE, flowDTO.getSuppliedTrigger());
			return eventOutput;
		}
		
		// delete all casinos for a game
		List<Casino> casinos;
		try {
			casinos = casinoService.findAllWhere("game", flowDTO.getSuppliedGameId());
		} catch (Exception e) {
			eventOutput = new EventOutput(EventOutput.Result.FAILURE, flowDTO.getSuppliedTrigger());
			return eventOutput;
		}
		for (Casino casino : casinos) {
			try {
				casinoService.deleteOne(casino);
			} catch (Exception e) {
				eventOutput = new EventOutput(EventOutput.Result.FAILURE, flowDTO.getSuppliedTrigger());
				return eventOutput;
			}
		}
		
		// delete the game
		try {
			gameService.deleteOne(gameToDelete);
		} catch (Exception e) {
			eventOutput = new EventOutput(EventOutput.Result.FAILURE, flowDTO.getSuppliedTrigger());
			return eventOutput;
		}
		
		// OK, set a trigger for EventOutput to trigger a transition in the state machine
		flowDTO.setCurrentCasino(null);
		flowDTO.setCurrentPlayer(null);
		
		eventOutput = new EventOutput(EventOutput.Result.SUCCESS);
		String message = String.format("DeleteCasinoForGameAndPlayerEvent do no transition");
		log.info(message);
		
		return eventOutput;
	}
	
	public interface DeleteCardGameEventDTO {
		
		String getSuppliedGameId();
		
		void setCurrentGame(Game game);
		
		String getSuppliedPlayerId();
		
		void setCurrentCasino(Casino casino);
		
		void setCurrentPlayer(Player player);
		
		CardGameStateMachine.Trigger getSuppliedTrigger();
		
	}
}
