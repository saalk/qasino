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
public class DetermineTurnResultsEventOld extends AbstractEvent {
	
	@Override
	protected EventOutput execution(final Object... eventInput) {
		
		DetermineTurnResultsEventDTOOld flowDTO = (DetermineTurnResultsEventDTOOld) eventInput[0];
		EventOutput eventOutput = null;
		
		String message = String.format("DetermineTurnResultsEventOld setCurrentGame: %s", flowDTO.getCurrentGame());
		log.info(message);
		message = String.format("DetermineTurnResultsEventOld CardAction: %s", flowDTO.getSuppliedCardAction());
		log.info(message);
		message = String.format("DetermineTurnResultsEventOld CardLocation: %s", flowDTO.getSuppliedCardLocation());
		log.info(message);
		
		Game gameToCheck = flowDTO.getCurrentGame();
		Casino casinoToCheck = flowDTO.getCurrentCasino();
		List<Casino> casinosToCheck = flowDTO.getCurrentCasinos();
		List<Hand> handsToCheck = flowDTO.getCurrentHands();
		
		message = String.format("DetermineTurnResultsEventOld handsToCheck: %s", handsToCheck);
		log.info(message);
		
		setNewCurrentTurn(flowDTO, casinoToCheck);
		int newBet = calculateNewBet(flowDTO);
		
		// determine raise and set bet
		try {
			
			// DOUBLE OR NOTHING
			
			if (flowDTO.getSuppliedCardAction() == CardAction.DEAL) {
				flowDTO.setNewBet(newBet);
				
				message = String.format("DetermineTurnResultsEventOld deal so only new bet and turn and raise: %s", newBet);
				log.info(message);
				
				eventOutput = new EventOutput(EventOutput.Result.SUCCESS);
				return eventOutput;
			}
			
			
			// IS 2nd CARD HIGHER? -> NOT FOR PASS
			boolean won = true;
			if (flowDTO.getSuppliedCardAction() == CardAction.HIGHER ||
					    flowDTO.getSuppliedCardAction() == CardAction.LOWER) {
				
				boolean higher = false;
				Card lastCard = handsToCheck.get(handsToCheck.size() - 1).getCard();
				
				message = String.format("DetermineTurnResultsEventOld lastCard: %s", lastCard);
				log.info(message);
				
				Card previousCard = handsToCheck.get(handsToCheck.size() - 2).getCard();
				
				message = String.format("DetermineTurnResultsEventOld previousCard: %s", previousCard);
				log.info(message);
				
				if (lastCard.getRank().getValue(gameToCheck.getGameType()) < previousCard.getRank().getValue(gameToCheck.getGameType())) {
					higher = false; // HIGHER
				} else {
					if (lastCard.getRank().getValue(gameToCheck.getGameType()) > previousCard.getRank().getValue(gameToCheck.getGameType())) {
						higher = true; // LOWER
					} else {
						higher = true; // EQUAL
						// TODO add gamevariant logic to this
					}
				}
				// UPDATE BET
				if ((higher) && (flowDTO.getSuppliedCardAction() == CardAction.HIGHER)) {
					message = String.format("DetermineTurnResultsEventOld won with raise: %s", newBet);
					log.info(message);
					flowDTO.setNewBet(newBet);
					won = true;
				} else {
					message = String.format("DetermineTurnResultsEventOld lost with raise: %s", newBet);
					log.info(message);
					flowDTO.setNewBet(-1 * newBet);
					won = false;
				}
			}
			
			
			// UPDATE CUBITS WITH BET
			if (((flowDTO.getSuppliedCardAction() == CardAction.PASS) && won) || !won) {
				
				message = String.format("DetermineTurnResultsEventOld update cubits: %s", flowDTO.getNewBet());
				log.info(message);
				flowDTO.setNewCubits(flowDTO.getNewBet());
				flowDTO.setCurrentPlayer(casinoToCheck.getPlayer());
				
				won = true;
			}
			
			// WHEN PASS OR LOST -> UPDATE NEW ACTIVE CASINO
			if ((flowDTO.getSuppliedCardAction() == CardAction.PASS) || !won) {
				
				boolean found2 = false;
				for (Casino casino : casinosToCheck) {
					if (found2) {
						flowDTO.setNewActiveCasino(Integer.parseInt(String.valueOf(casino.getCasinoId())));
						found2 = false;
						break;
					}
					if (casino.getCasinoId() == Integer.parseInt(flowDTO.getSuppliedCasinoId())) {
						found2 = true;
						message = String.format("DetermineTurnResultsEventOld getSuppliedCasinoId found: %s", flowDTO.getSuppliedCasinoId());
						log.info(message);
					}
				}
				if (found2) {
					flowDTO.setNewActiveCasino(casinosToCheck.get(0).getCasinoId());
					message = String.format("DetermineTurnResultsEventOld setNewActiveCasino : %s", flowDTO.getNewActiveCasino());
					log.info(message);
				}
			}
			
			message = String.format("DetermineTurnResultsEventOld setNewBet: %s", flowDTO.getNewBet());
			log.info(message);
			
			
		} catch (Exception e) {
			message = String.format("DetermineTurnResultsEventOld crash : %s", e);
			log.info(message);
			eventOutput = new EventOutput(EventOutput.Result.FAILURE, CardGameStateMachine.Trigger.ERROR);
			return eventOutput;
		}
		
		eventOutput = new EventOutput(EventOutput.Result.SUCCESS);
		message = String.format("DetermineTurnResultsEventOld do no transition");
		log.info(message);
		
		return eventOutput;
	}
	
	private int calculateNewBet(DetermineTurnResultsEventDTOOld flowDTO) {
		int raiseFactor;
		if (flowDTO.getNewCurrentTurn() > 2) {
			raiseFactor = (int) Math.pow(2, flowDTO.getNewCurrentTurn() - 1);
		} else {
			raiseFactor = flowDTO.getNewCurrentTurn();
		}
		return raiseFactor * flowDTO.getCurrentGame().getAnte();
	}
	
	private void setNewCurrentTurn(DetermineTurnResultsEventDTOOld flowDTO, Casino casinoToCheck) {
		
		// based on the card action and the active turn -> set the new current turn
		if (flowDTO.getSuppliedCardAction() == CardAction.DEAL) {
			flowDTO.setNewCurrentTurn(1);
		} else if (flowDTO.getSuppliedCardAction() == CardAction.HIGHER ||
				           flowDTO.getSuppliedCardAction() == CardAction.LOWER) {
			flowDTO.setNewCurrentTurn(casinoToCheck.getActiveTurn() + 1);
		} else {
			// PASS
			flowDTO.setNewCurrentTurn(casinoToCheck.getActiveTurn());
		}
		
		String message = String.format("DetermineTurnResultsEventOld setNewCurrentTurn is: %s", flowDTO.getNewCurrentTurn());
		log.info(message);
		
	}
	
	public interface DetermineTurnResultsEventDTOOld {
		
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
