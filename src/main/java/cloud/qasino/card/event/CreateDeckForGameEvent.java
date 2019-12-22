package cloud.qasino.card.event;

import cloud.qasino.card.entity.Card;
import cloud.qasino.card.entity.Deck;
import cloud.qasino.card.entity.Game;
import cloud.qasino.card.entity.enums.CardLocation;
import cloud.qasino.card.event.common.AbstractEvent;
import cloud.qasino.card.event.common.EventOutput;
import cloud.qasino.card.service.IDeckService;
import cloud.qasino.card.statemachine.CardGameStateMachine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class CreateDeckForGameEvent extends AbstractEvent {
	
	// @Resource = javax, @Inject = javax, @Autowire = spring bean factory
	@Autowired
	private IDeckService deckService;
	
	@Override
	protected EventOutput execution(final Object... eventInput) {
		
		CreateDeckForGameEventDTO flowDTO = (CreateDeckForGameEventDTO) eventInput[0];
		EventOutput eventOutput;
		String message;
		
		Game gameToUpdate = flowDTO.getCurrentGame();
		List<Card> cards = Card.newDeck(Integer.parseInt(flowDTO.getSuppliedJokers()));
		if (!flowDTO.getSuppliedTest().equals("true")) {
			Collections.shuffle(cards);
			message = String.format("CreateDeckForGameEvent do shuffle # newDeck with cards is: %s", cards.size());
			log.info(message);
		}
		
		int order = 1;
		List<Deck> newDeckDtos = new ArrayList<>();
		for (Card card : cards) {
			Deck newDeck = new Deck();
			newDeck.setCard(card);
			newDeck.setCardOrder(order++);
			newDeck.setDealtTo(null);
			newDeck.setGame(gameToUpdate);
			newDeck.setCardLocation(CardLocation.STACK);
			newDeckDtos.add(newDeck);
		}
		try {
			List<Deck> createdDecks = deckService.createAll(newDeckDtos);
			if (createdDecks == null) {
				eventOutput = new EventOutput(EventOutput.Result.FAILURE, CardGameStateMachine.Trigger.ERROR);
				return eventOutput;
			}
			flowDTO.setCurrentDecks(createdDecks);
		} catch (Exception e) {
			eventOutput = new EventOutput(EventOutput.Result.FAILURE, CardGameStateMachine.Trigger.ERROR);
			return eventOutput;
		}
		flowDTO.setNewActiveCasino(flowDTO.getCurrentGame().getCasinos().get(0).getCasinoId());
		
		if (flowDTO.getSuppliedTrigger() == CardGameStateMachine.Trigger.POST_SHUFFLE) {
			// key event so do a transition
			eventOutput = new EventOutput(EventOutput.Result.SUCCESS, flowDTO.getSuppliedTrigger());
			message = String.format("CreateDeckForGameEvent do a transition with trigger is: %s", flowDTO.getSuppliedTrigger());
			log.info(message);
		} else {
			eventOutput = new EventOutput(EventOutput.Result.SUCCESS);
			message = String.format("CreateDeckForGameEvent do no transition");
			log.info(message);
		}
		return eventOutput;
	}
	
	public interface CreateDeckForGameEventDTO {
		
		// all game fields
		String getSuppliedGameId();
		
		void setCurrentGame(Game game);
		
		Game getCurrentGame();
		
		String getSuppliedJokers();
		
		String getSuppliedTest();
		
		void setNewActiveCasino(int activeCasino);
		
		void setCurrentDecks(List<Deck> decks);
		
		CardGameStateMachine.Trigger getSuppliedTrigger();
		
	}
}
