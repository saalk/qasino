package cloud.qasino.card.vo;

import cloud.qasino.card.entity.enums.AiLevel;
import cloud.qasino.card.entity.enums.Avatar;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.text.WordUtils;
import org.springframework.hateoas.server.core.Relation;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Relation(value = "visitor", collectionRelation = "visitors")
//@JsonIdentityInfo(generator=JSOGGenerator.class)
public class CardGamePlayer implements Serializable {
	
	// Player has 9 fields, PlayerDto has 2 more
	// discard lombok setter for this field -> make your own
	@JsonIgnore
	@Setter(AccessLevel.NONE)
	private String name; // extra field "Script Joe(Human|Smart) [Elf]"
	@JsonProperty(value = "visitorId")
	private int playerId;
	// private String created; to prevent setting, this is generated
	private String alias;
	@Setter(AccessLevel.NONE)
	private String human; // changed field for boolean
	private String aiLevel;
	@JsonIgnore
	private String avatar;
	private int cubits;
	private int securedLoan;
	
	//@JsonBackReference(value="playerDto")
	//@JsonProperty(value = "games")
	@Setter(AccessLevel.NONE)
	@JsonIgnore
	private List<CardGame> cardGames;
	@JsonIgnore
	@Setter(AccessLevel.NONE)
	private int winCount; // extra field
	@Setter(AccessLevel.NONE)
	@JsonIgnore
	private List<CardGameCasino> cardGameCasinos;
	
	public CardGamePlayer() {
	}
	
	@JsonIgnore
	public Avatar getAvatarConvertedFromLabel(String avatarLabel) throws Exception {
		Avatar converted = Avatar.fromLabel(avatar);
		if (converted == null) {
			throw new Exception("AvatarParseLabelException");
		}
		return converted;
	}
	
	public void setAvatar(Avatar avatar) {
		// static Eum methods:
		// - valueOf() - returns enum instance taking a String
		// - values()  - returns all enum instances
		// instance Enum method:
		// - name()    - returns name of enum constant
		// -> better use toString() to get the user-friendly name
		this.avatar = (String.valueOf(avatar));
	}
	
	@JsonIgnore
	public AiLevel getAiLevelConvertedFromLabel(String aiLevelLabel) throws Exception {
		AiLevel converted = AiLevel.fromLabel(aiLevelLabel);
		if (converted == null) {
			throw new Exception("AiLevelParseLabelException");
		}
		return converted;
	}
	
	public void setAiLevel(AiLevel aiLevel) {
		// static Eum methods:
		// - valueOf() - returns enum instance taking a String
		// - values()  - returns all enum instances
		// instance Enum method:
		// - name()    - returns name of enum constant
		// -> better use toString() to get the user-friendly name
		this.aiLevel = (String.valueOf(aiLevel));
	}
	
	public void setName() {
		// "Script Joe(Human|Smart) [Elf]"
		this.name = this.alias + " [" + WordUtils.capitalizeFully(this.aiLevel) + "]";
	}
	
	public void setWinCount() {
		if (cardGames != null) {
			this.winCount = cardGames.size();
		} else {
			this.winCount = 0;
		}
	}
	
	public void setHuman(boolean human) {
		this.human = String.valueOf(human);
	}
	
	public String getHuman() {
		return this.human;
	}
	
	public void setCardGames(List<CardGame> cardGames) {
		this.winCount = cardGames == null ? 0 : cardGames.size();
		this.cardGames = cardGames;
	}
	
	public List<CardGame> getCardGames() {
		return this.cardGames;
	}
	
	public void setCardGameCasinos(List<CardGameCasino> casinoDtos) {
		this.cardGameCasinos = casinoDtos;
	}
	
	public List<CardGameCasino> getCardGameCasinos() {
		return this.cardGameCasinos;
	}
	
}
