package nl.knikit.card.event;

import lombok.extern.slf4j.Slf4j;
import nl.knikit.card.entity.Casino;
import nl.knikit.card.entity.Deck;
import nl.knikit.card.entity.Game;
import nl.knikit.card.entity.Hand;
import nl.knikit.card.entity.enums.CardAction;
import nl.knikit.card.entity.enums.CardLocation;
import nl.knikit.card.event.common.AbstractEvent;
import nl.knikit.card.event.common.EventOutput;
import nl.knikit.card.mapper.ModelMapperUtil;
import nl.knikit.card.statemachine.CardGameStateMachine;
import nl.knikit.card.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class VerifyTurnStateEvent extends AbstractEvent {
	
	// @Resource = javax, @Inject = javax, @Autowire = spring bean factory
	@Autowired
	private IGameService gameService;
	
	@Autowired
	private IPlayerService playerService;
	
	@Autowired
	private ICasinoService casinoService;
	
	@Autowired
	private ICardService cardService;
	
	@Autowired
	private IHandService handService;
	
	@Autowired
	private ModelMapperUtil mapUtil;
	
	@Override
	protected EventOutput execution(final Object... eventInput) {
		
		VerifyTurnStateEventDTO flowDTO = (VerifyTurnStateEventDTO) eventInput[0];
		EventOutput eventOutput = null;
		
		String message = String.format("VerifyTurnStateEvent setCurrentGame: %s", flowDTO.getCurrentGame());
		log.info(message);
		
		message = String.format("VerifyTurnStateEvent CardAction: %s", flowDTO.getSuppliedCardAction());
		log.info(message);
		
		message = String.format("VerifyTurnStateEvent CardLocation: %s", flowDTO.getSuppliedCardLocation());
		log.info(message);
		
		// init all the object and lists
		Game gameToCheck = flowDTO.getCurrentGame();
		Casino casinoToCheck = flowDTO.getCurrentCasino();
		
		List<Casino> casinosToCheck = flowDTO.getCurrentCasinos();
		List<Hand> handsToCheck = flowDTO.getCurrentHands();
		List<Deck> decksToCheck = flowDTO.getCurrentDecks();
		
		// do the check
		//		// do not do HIGHER, LOWER when the current casino in the game is not the casino supplied
//		if (((flowDTO.getSuppliedCardAction().equals(CardAction.HIGHER))
//				    || (flowDTO.getSuppliedCardAction().equals(CardAction.LOWER))) &&
//				    (gameToCheck.getActiveCasino()!= Integer.parseInt(flowDTO.getSuppliedCasinoId()))  )  {
//			eventOutput = new EventOutput(EventOutput.Result.SUCCESS);
//
//			message = String.format("UpdateDeckForGameAndCasinoEvent switch to different casino when playing is not allowed");
//			log.info(message);
//
//			return eventOutput;
//		}

//		// when current round is zero do not DEAL to casinos other than having playingOrder 1
//		if (((flowDTO.getSuppliedCardAction().equals(CardAction.DEAL)) && (gameToCheck.getCurrentRound()==0) &&
//				    (dealToThisCasino.getPlayingOrder()!=1)))  {
//			eventOutput = new EventOutput(EventOutput.Result.SUCCESS);
//
//			message = String.format("UpdateDeckForGameAndCasinoEvent switch to not first casino when dealing is not allowed");
//			log.info(message);
//
//			return eventOutput;
//		}
		
		try {
			
			for (Deck deck : decksToCheck) {

			/** check if
			1. casino supplied is the casino that is allowed to turn
			2. action is correct for player: human or ai
			3. when human
			- HIGHER or LOWER -> check if lastHand is DEAL
			- PASS -> check if lastHands starting from DEAL are equal or more than (min turns)
			- cardTotal -> check if card total is correct for gametype
			4. when ai
			-
			**/
				
			}
		} catch (Exception e) {
			eventOutput = new EventOutput(EventOutput.Result.FAILURE, flowDTO.getSuppliedTrigger());
			return eventOutput;
		}
		
		eventOutput = new EventOutput(EventOutput.Result.SUCCESS);
		message = String.format("VerifyTurnStateEvent do no transition");
		log.info(message);
		
		return eventOutput;
	}
	
	public interface VerifyTurnStateEventDTO {
		
		// all game and trigger fields
		String getSuppliedGameId();
		
		CardGameStateMachine.Trigger getSuppliedTrigger();
		
		Game getCurrentGame();
		
		
		List<Casino> getCurrentCasinos();
		
		List<Deck> getCurrentDecks();
		
		List<Hand> getCurrentHands();
		
		Casino getCurrentCasino();
		
		void setCurrentGame(Game game);
		
		void setSuppliedTrigger(CardGameStateMachine.Trigger trigger);
		
		// the rest of the supplied fields
		String getSuppliedCasinoId();
		
		CardAction getSuppliedCardAction();
		
		String getSuppliedTotal();
		
		CardLocation getSuppliedCardLocation();
		
	}
}
