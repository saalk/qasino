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
import nl.knikit.card.statemachine.CardGameStateMachine;
import nl.knikit.card.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class CreateHandForCasinoForGameAndPlayerEvent extends AbstractEvent {
	
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
	
	@Override
	protected EventOutput execution(final Object... eventInput) {
		
		CreateHandForCasinoForGameAndPlayerEventDTO flowDTO = (CreateHandForCasinoForGameAndPlayerEventDTO) eventInput[0];
		EventOutput eventOutput;
		String message;
		
		// NO CARDS LEFT
		if (flowDTO.getSuppliedTrigger() == CardGameStateMachine.Trigger.NO_CARDS_LEFT) {
			// no key event no a transition
			eventOutput = new EventOutput(EventOutput.Result.SUCCESS);
			message = String.format("CreateHandForCasinoForGameAndPlayerEvent no transition with trigger is: %s", flowDTO.getSuppliedTrigger());
			log.info(message);
			return eventOutput;
		}
		
		// INIT
		Casino dealToThisCasino = flowDTO.getCurrentCasino();
		List<Hand> otherHandsForCasino = flowDTO.getCurrentHands();
		Hand handToCreate = new Hand();
		List<Hand> handsCreated = new ArrayList<>();
		
		// ADD
		try {
			
			message = String.format("CreateHandForCasinoForGameAndPlayerEvent getDecks is: %s", flowDTO.getCurrentDecks());
			log.info(message);
			
			int cardOrder = otherHandsForCasino.size() + 1;
			for (Deck deck : flowDTO.getCurrentDecks()) {
				
				handToCreate.setCasino(dealToThisCasino);
				handToCreate.setPlayer(dealToThisCasino.getPlayer());
				handToCreate.setCard(deck.getCard());
				handToCreate.setCardOrder(cardOrder++);
				
				if (flowDTO.getNewCurrentTurn() != 0) {
					handToCreate.setTurn(flowDTO.getNewCurrentTurn());
				}
				if (flowDTO.getNewCurrentRound() != 0) {
					handToCreate.setRound(flowDTO.getNewCurrentRound());
				}
				if (flowDTO.getSuppliedCardLocation() != null) {
					handToCreate.setCardLocation(flowDTO.getSuppliedCardLocation());
				}
				if (flowDTO.getSuppliedCardAction() != null) {
					handToCreate.setCardAction(flowDTO.getSuppliedCardAction());
				}
				handsCreated.add(handService.create(handToCreate));
				
				message = String.format("CreateHandForCasinoForGameAndPlayerEvent handToCreate is: %s", handToCreate.toString());
				log.info(message);
			}
		} catch (Exception e) {
			eventOutput = new EventOutput(EventOutput.Result.FAILURE, CardGameStateMachine.Trigger.ERROR);
			return eventOutput;
		}
		otherHandsForCasino.addAll(handsCreated);
		flowDTO.setCurrentHands(otherHandsForCasino);
		
		if ((flowDTO.getSuppliedTrigger() == CardGameStateMachine.Trigger.PUT_DEAL_TURN) ||
				    (flowDTO.getSuppliedTrigger() == CardGameStateMachine.Trigger.PUT_PLAYING_TURN)) {
			// key event so do a transition
			eventOutput = new EventOutput(EventOutput.Result.SUCCESS, flowDTO.getSuppliedTrigger());
			message = String.format("CreateHandForCasinoForGameAndPlayerEvent do a transition with trigger is: %s", flowDTO.getSuppliedTrigger());
			log.info(message);
		} else {
			eventOutput = new EventOutput(EventOutput.Result.SUCCESS);
			message = String.format("CreateHandForCasinoForGameAndPlayerEvent do no transition");
			log.info(message);
		}
		return eventOutput;
	}
	
	public interface CreateHandForCasinoForGameAndPlayerEventDTO {
		
		// all game and trigger fields
		String getSuppliedGameId();
		
		CardGameStateMachine.Trigger getSuppliedTrigger();
		
		Game getCurrentGame();
		
		Casino getCurrentCasino();
		
		List<Hand> getCurrentHands();
		
		void setCurrentGame(Game game);
		
		// the rest of the supplied fields
		CardAction getSuppliedCardAction();
		
		CardLocation getSuppliedCardLocation();
		
		// get the data created by other events
		
		List<Deck> getCurrentDecks();
		
		int getNewCurrentRound();
		
		int getNewCurrentTurn();
		
		// pass on the data created here for other events
		
		void setCurrentHands(List<Hand> hands);
	}
}
