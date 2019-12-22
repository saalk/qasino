
package cloud.qasino.card.mapper;

import cloud.qasino.card.entity.Game;
import cloud.qasino.card.dto.GameDto;
import org.modelmapper.PropertyMap;

/* source is entity from DAO layer
	Basic int/String:
	- gameId;
	- ante;
	Enums:
	- STATE;        next Triggers?
	- TYPE;
	Specials:
	- round;        minRounds;currentRound;activeCasino;
	- turn;         minTurns;currentTurn;turnsToWin;maxTurns;
	Objects:
	- List<Decks> Decks;   deckId;game;card;cardOrder;dealtToDto;
	- Player winner;       alias;AVATAR
	
	*/

// PropertyMap Has < source to get, destination to set>
public class GameMapFromDto extends PropertyMap<GameDto, Game> {
	@Override
	protected void configure() {
		// set the game.player suppliedPlayerId with a get from DTO winner fields
		// map().getPlayer().setSuppliedPlayerId(source.getWinner().getSuppliedPlayerId());
	}
};