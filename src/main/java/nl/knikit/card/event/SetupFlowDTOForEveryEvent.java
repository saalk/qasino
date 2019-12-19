package nl.knikit.card.event;

import lombok.extern.slf4j.Slf4j;
import nl.knikit.card.entity.*;
import nl.knikit.card.entity.enums.CardAction;
import nl.knikit.card.event.common.AbstractEvent;
import nl.knikit.card.event.common.EventOutput;
import nl.knikit.card.statemachine.CardGameStateMachine;
import nl.knikit.card.service.ICasinoService;
import nl.knikit.card.service.IGameService;
import nl.knikit.card.service.IPlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Component
@Slf4j
public class SetupFlowDTOForEveryEvent extends AbstractEvent {
	
	// @Resource = javax, @Inject = javax, @Autowire = spring bean factory
	@Autowired
	private IGameService gameService;
	
	@Autowired
	private IPlayerService playerService;
	
	@Autowired
	private ICasinoService casinoService;
//
//	@Autowired
//	private IHandService handService;
//
//	@Autowired
//	private IDeckService deckService;
	
	@Override
	protected EventOutput execution(final Object... eventInput) {
		
		SetupFlowDTOForEveryEventDTO flowDTO = (SetupFlowDTOForEveryEventDTO) eventInput[0];
		EventOutput eventOutput;
		String message;
		
		message = String.format("SetupFlowDTOForEveryEvent getSuppliedGameId id : %s", flowDTO.getSuppliedGameId());
		log.info(message);
		
		// get the game and decks and casinos sorted in flow DTO if supplied
		if ((flowDTO.getSuppliedGameId() != null) && (Integer.parseInt(flowDTO.getSuppliedGameId()) > 0)) {
			try {
				
				// game
				flowDTO.setCurrentGame(gameService.findOne(Integer.parseInt(flowDTO.getSuppliedGameId())));
				
				message = String.format("SetupFlowDTOForEveryEvent game found: %s", flowDTO.getCurrentGame());
				log.info(message);
				
				if (flowDTO.getCurrentGame() == null) {
					eventOutput = new EventOutput(EventOutput.Result.FAILURE, flowDTO.getSuppliedTrigger());
					return eventOutput;
				}
				
				// decks
				List<Deck> unsortedDecks = flowDTO.getCurrentGame().getDecks();
				
				message = String.format("SetupFlowDTOForEveryEvent decks unsortedDecks: %s", unsortedDecks);
				log.info(message);
				
				if (unsortedDecks != null && !unsortedDecks.isEmpty()) {
					Collections.sort(unsortedDecks, Comparator.comparing(Deck::getCardOrder).thenComparing(Deck::getCardOrder));
					flowDTO.setCurrentDecks(unsortedDecks);
					
					message = String.format("SetupFlowDTOForEveryEvent decks found: %s", flowDTO.getCurrentDecks());
					log.info(message);
				}
					
				// casinos
				List<Casino> unsortedCasinos = flowDTO.getCurrentGame().getCasinos();
				
				message = String.format("SetupFlowDTOForEveryEvent decks unsortedCasinos: %s", unsortedCasinos);
				log.info(message);
				
				if (unsortedCasinos != null && !unsortedCasinos.isEmpty()) {
					Collections.sort(unsortedCasinos, Comparator.comparing(Casino::getPlayingOrder).thenComparing(Casino::getPlayingOrder));
					flowDTO.setCurrentCasinos(unsortedCasinos);
					
					message = String.format("SetupFlowDTOForEveryEvent casinos found: %s", flowDTO.getCurrentCasinos());
					log.info(message);
				}
				
			} catch (Exception e) {
				message = String.format("SetupFlowDTOForEveryEvent error game decks or casinos: %s", e);
				log.info(message);
				
				eventOutput = new EventOutput(EventOutput.Result.FAILURE, flowDTO.getSuppliedTrigger());
				return eventOutput;
			}
		}
		
		// get the casino and hands sorted in flow DTO if supplied
		if ((flowDTO.getSuppliedCasinoId() != null) && (Integer.parseInt(flowDTO.getSuppliedCasinoId()) > 0)) {
			try {
				
				flowDTO.setCurrentCasino(casinoService.findOne(Integer.parseInt(flowDTO.getSuppliedCasinoId())));
				if (flowDTO.getCurrentCasino() == null) {
					eventOutput = new EventOutput(EventOutput.Result.FAILURE, CardGameStateMachine.Trigger.ERROR);
					return eventOutput;
				}
				
				// hands
				List<Hand> unsortedHands = flowDTO.getCurrentCasino().getHands();
				Collections.sort(unsortedHands, Comparator.comparing(Hand::getCardOrder).thenComparing(Hand::getCardOrder));
				flowDTO.setCurrentHands(unsortedHands);
				message = String.format("SetupFlowDTOForEveryEvent hands: %s", flowDTO.getCurrentHands());
				log.info(message);
				
			} catch (Exception e) {
				message = String.format("SetupFlowDTOForEveryEvent error casino or hands: %s", e);
				log.info(message);
				
				eventOutput = new EventOutput(EventOutput.Result.FAILURE, CardGameStateMachine.Trigger.ERROR);
				return eventOutput;
			}
		}
		
		// get the player in flow DTO if supplied
		if ((flowDTO.getSuppliedPlayerId() != null) && (Integer.parseInt(flowDTO.getSuppliedPlayerId()) > 0)) {
			try {
				flowDTO.setCurrentPlayer(playerService.findOne(Integer.parseInt(flowDTO.getSuppliedPlayerId())));
				if (flowDTO.getCurrentPlayer() == null) {
					eventOutput = new EventOutput(EventOutput.Result.FAILURE, CardGameStateMachine.Trigger.ERROR);
					return eventOutput;
				}
				message = String.format("SetupFlowDTOForEveryEvent player: %s", flowDTO.getCurrentPlayer());
				log.info(message);
			} catch (Exception e) {
				message = String.format("SetupFlowDTOForEveryEvent error player: %s", e);
				log.info(message);
				
				eventOutput = new EventOutput(EventOutput.Result.FAILURE, flowDTO.getSuppliedTrigger());
				return eventOutput;
			}
		}
		
		// DETERMINE CURRENT ROUND FOR GAME AND HAND
		if (flowDTO.getSuppliedCardAction() == CardAction.DEAL &&
				    flowDTO.getCurrentCasino().getPlayingOrder() == 1) {
			flowDTO.setNewCurrentRound(flowDTO.getCurrentGame().getCurrentRound() + 1);
			message = String.format("SetupFlowDTOForEveryEvent getCurrentRound + 1: %s", flowDTO.getNewCurrentRound());
			log.info(message);
		} else {
			flowDTO.setNewCurrentRound(flowDTO.getCurrentGame().getCurrentRound());
		}
		
		
		eventOutput = new EventOutput(EventOutput.Result.SUCCESS);
		message = String.format("SetupFlowDTOForEveryEvent success: %s", flowDTO.getSuppliedTrigger());
		log.info(message);
		return eventOutput;
	}
	
	public interface SetupFlowDTOForEveryEventDTO {
		
		CardGameStateMachine.Trigger getSuppliedTrigger();
		
		// all supplied fields
		String getSuppliedGameId();
		
		String getSuppliedCasinoId();
		
		String getSuppliedPlayerId();
		
		CardAction getSuppliedCardAction();
		
		void setNewCurrentRound(int currentRound);
		
		int getNewCurrentRound();
		
		// all the retrieved data
		Game getCurrentGame();
		
		void setCurrentGame(Game game);
		
		List<Deck> getCurrentDecks();
		
		void setCurrentDecks(List<Deck> decks);
		
		List<Casino> getCurrentCasinos();
		
		void setCurrentCasinos(List<Casino> casinos);
		
		Casino getCurrentCasino();
		
		void setCurrentCasino(Casino casino);
		
		List<Hand> getCurrentHands();
		
		void setCurrentHands(List<Hand> hands);
		
		Player getCurrentPlayer();
		
		void setCurrentPlayer(Player player);
		
	}
}
