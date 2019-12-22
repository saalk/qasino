package cloud.qasino.card.event;

import cloud.qasino.card.entity.*;
import cloud.qasino.card.entity.enums.CardAction;
import cloud.qasino.card.entity.enums.CardLocation;
import cloud.qasino.card.event.common.AbstractEvent;
import cloud.qasino.card.event.common.EventOutput;
import cloud.qasino.card.statemachine.CardGameStateMachine;
import lombok.extern.slf4j.Slf4j;
import nl.knikit.card.entity.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class DetermineTurnResultsEvent extends AbstractEvent {

	@Override
	protected EventOutput execution(final Object... eventInput) {
		
		DetermineTurnResultsEventDTO flowDTO = (DetermineTurnResultsEventDTO) eventInput[0];
		EventOutput eventOutput = null;
		String message;
		
		message = String.format("DetermineTurnResultsEventOld CardAction: %s", flowDTO.getSuppliedCardAction());
		log.info(message);
		message = String.format("DetermineTurnResultsEventOld CardLocation: %s", flowDTO.getSuppliedCardLocation());
		log.info(message);
		message = String.format("DetermineTurnResultsEventOld handsToCheck: %s", flowDTO.getCurrentHands());
		log.info(message);
		
		Game gameToCheck = flowDTO.getCurrentGame();
		Casino casinoToCheck = flowDTO.getCurrentCasino();
		List<Casino> casinosToCheck = flowDTO.getCurrentCasinos();
		List<Hand> handsToCheck = flowDTO.getCurrentHands();
		
		boolean endTurn = false;
		
		// set the new bet and turn
		switch (flowDTO.getSuppliedCardAction()) {
			case CardAction.DEAL:
				flowDTO.setNewCurrentTurn(1);
				flowDTO.setNewBet(calculateNewBet(flowDTO));
				break;
			case HIGHER:
			case LOWER:
				flowDTO.setNewCurrentTurn(casinoToCheck.getActiveTurn() + 1);
				if (isWon(flowDTO, gameToCheck, handsToCheck)) {
					flowDTO.setNewBet(calculateNewBet(flowDTO));
				} else {
					flowDTO.setNewBet(-1 * calculateNewBet(flowDTO));
					endTurn = true;
				}
				break;
			case PASS:
				flowDTO.setNewCurrentTurn(casinoToCheck.getActiveTurn());
				flowDTO.setNewBet(casinoToCheck.getBet());
				endTurn = true;
				break;
			case NEXT:
				//TODO based on ai level determine higher and lower before this switch!!
				break;
		}
		
		if (endTurn) {
			// this will cause a player update
			flowDTO.setNewCubits(flowDTO.getNewBet());
			flowDTO.setCurrentPlayer(casinoToCheck.getPlayer());
			
			setNewActiveCasino(flowDTO, casinosToCheck);
		}
		
		eventOutput = new EventOutput(EventOutput.Result.SUCCESS);
		return eventOutput;
	}
	
	private boolean isWon(DetermineTurnResultsEventDTO flowDTO, Game gameToCheck, List<Hand> handsToCheck) {
		
		boolean newCardIsHigher;
		
		Card lastCard = handsToCheck.get(handsToCheck.size() - 1).getCard();
		Card previousCard = handsToCheck.get(handsToCheck.size() - 2).getCard();
		
		if (lastCard.getRank().getValue(gameToCheck.getGameType()) < previousCard.getRank().getValue(gameToCheck.getGameType())) {
			newCardIsHigher = false; // HIGHER
		} else {
			if (lastCard.getRank().getValue(gameToCheck.getGameType()) > previousCard.getRank().getValue(gameToCheck.getGameType())) {
				newCardIsHigher = true; // LOWER
			} else {
				newCardIsHigher = true; // EQUAL
				// TODO add gamevariant logic to this
			}
		}
		return (newCardIsHigher) && (flowDTO.getSuppliedCardAction() == CardAction.HIGHER);
	}
	
	private int calculateNewBet(DetermineTurnResultsEventDTO flowDTO) {
		int raiseFactor;
		if (flowDTO.getNewCurrentTurn() > 2) {
			raiseFactor = (int) Math.pow(2, flowDTO.getNewCurrentTurn() - 1);
		} else {
			raiseFactor = flowDTO.getNewCurrentTurn();
		}
		return raiseFactor * flowDTO.getCurrentGame().getAnte();
	}
	
	private void setNewActiveCasino(DetermineTurnResultsEventDTO flowDTO, List<Casino> casinosToCheck) {
		boolean found = false;
		for (Casino casino : casinosToCheck) {
			if (found) {
				flowDTO.setNewActiveCasino(Integer.parseInt(String.valueOf(casino.getCasinoId())));
				found = false;
				break;
			}
			if (casino.getCasinoId() == Integer.parseInt(flowDTO.getSuppliedCasinoId())) {
				found = true;
			}
		}
		if (found) {
			flowDTO.setNewActiveCasino(casinosToCheck.get(0).getCasinoId());
		}
	}
	
	public interface DetermineTurnResultsEventDTO {
		
		// all game and trigger fields
		String getSuppliedGameId();
		
		CardGameStateMachine.Trigger getSuppliedTrigger();
		
		Game getCurrentGame();
		
		List<Casino> getCurrentCasinos();
		
		List<Hand> getCurrentHands();
		
		Casino getCurrentCasino();
		
		void setCurrentGame(Game game);
		
		// the rest of the supplied fields
		String getSuppliedCasinoId();
		
		CardAction getSuppliedCardAction();
		
		CardLocation getSuppliedCardLocation();
		
		// get the data created by other events
		
		void setNewCurrentTurn(int currentTurn);
		
		void setCurrentPlayer(Player player);
		
		int getNewCurrentTurn();
		
		void setNewBet(int bet);
		
		int getNewBet();
		
		void setNewActiveCasino(int activeCasino);
		
		int getNewActiveCasino();
		
		void setNewCubits(int cubits);
	}
}
