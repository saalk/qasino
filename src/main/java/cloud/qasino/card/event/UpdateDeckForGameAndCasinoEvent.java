package cloud.qasino.card.event;

import cloud.qasino.card.entity.Casino;
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
import java.util.List;

@Component
@Slf4j
public class UpdateDeckForGameAndCasinoEvent extends AbstractEvent {
	
	// @Resource = javax, @Inject = javax, @Autowire = spring bean factory
//	@Autowired
//	private IGameService gameService;
//
//	@Autowired
//	private ICasinoService casinoService;
//
	@Autowired
	private IDeckService deckService;
	
	@Override
	protected EventOutput execution(final Object... eventInput) {
		
		UpdateDeckForGameAndCasinoEventDTO flowDTO = (UpdateDeckForGameAndCasinoEventDTO) eventInput[0];
		EventOutput eventOutput;
		
		// INIT
		Casino dealToThisCasino = flowDTO.getCurrentCasino();
		List<Deck> allDecksForGame = flowDTO.getCurrentDecks();
		List<Deck> allDecksToUpdate = new ArrayList<>();
		List<Deck> decksUpdated = new ArrayList<>();
		
		String message = String.format("UpdateDeckForGameAndCasinoEvent getSuppliedCasinoId is: %s", flowDTO.getSuppliedCasinoId());
		log.info(message);
		
		// CHECK SUPPLIED TOTAL
		int total = Integer.parseInt(flowDTO.getSuppliedTotal());
		for (Deck deck : allDecksForGame) {
			if (deck.getCardLocation() == CardLocation.STACK && total > 0) {
				allDecksToUpdate.add(deck);
				total -= 1;
			}
		}
		
		// NO CARDS LEFT
		if (allDecksToUpdate.size() == 0 && (total > 0)) {
			message = String.format("UpdateDeckForGameAndCasinoEvent allDecksToUpdate is zero, no cards left is but needed: %s", total);
			log.info(message);
			flowDTO.setSuppliedTrigger(CardGameStateMachine.Trigger.NO_CARDS_LEFT);
			eventOutput = new EventOutput(EventOutput.Result.FAILURE, flowDTO.getSuppliedTrigger());
			return eventOutput;
		}
		
		// UPDATE
		try {
			
			for (Deck deck : allDecksToUpdate) {
				
				deck.setDealtTo(dealToThisCasino);
				deck.setCardLocation(flowDTO.getSuppliedCardLocation());
				deck = deckService.update(deck);
				decksUpdated.add(deck);
				message = String.format("UpdateDeckForGameAndCasinoEvent deckToUpdate is: %s", deck.toString());
				log.info(message);
			}
		} catch (Exception e) {
			eventOutput = new EventOutput(EventOutput.Result.FAILURE, flowDTO.getSuppliedTrigger());
			return eventOutput;
		}
		flowDTO.setCurrentDecks(decksUpdated);

		
		eventOutput = new EventOutput(EventOutput.Result.SUCCESS);
		message = String.format("UpdateDeckForGameAndCasinoEvent do no transition");
		log.info(message);
		
		return eventOutput;
	}
	
	public interface UpdateDeckForGameAndCasinoEventDTO {
		
		// all game and trigger fields
		String getSuppliedGameId();
		
		CardGameStateMachine.Trigger getSuppliedTrigger();
		
		Game getCurrentGame();
		
		List<Deck> getCurrentDecks();
		
		Casino getCurrentCasino();
		
		void setCurrentGame(Game game);
		
		void setSuppliedTrigger(CardGameStateMachine.Trigger trigger);
		
		// the rest of the supplied fields and data stored by other events
		String getSuppliedCasinoId();
		
		String getSuppliedTotal();
		
		CardLocation getSuppliedCardLocation();
		
		// pass on the data created here to other events
		
		void setCurrentDecks(List<Deck> decks);
		
	}
}
