package cloud.qasino.card.dto;

import cloud.qasino.card.entity.Player;
import cloud.qasino.card.entity.enums.AiLevel;
import cloud.qasino.card.entity.enums.Avatar;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import org.springframework.hateoas.server.core.Relation;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Relation(value = "visitor", collectionRelation = "visitors")
//@JsonIdentityInfo(generator=JSOGGenerator.class)
public class PlayerDto implements Serializable {
	
	// Player has 9 fields, PlayerDto has 2 more
	// discard lombok setter for this field -> make your own
	@Setter(AccessLevel.NONE)
	private String name; // extra field "Script Joe(Human|Smart) [Elf]"
	@JsonProperty(value = "playerId")
	private int playerId;
	// private String created; to prevent setting, this is generated
	private String alias;
	@Setter(AccessLevel.NONE)
	private String human; // changed field for boolean
	private String aiLevel;
	private String avatar;
	private int cubits;
	private int securedLoan;
	
	//@JsonBackReference(value="playerDto")
	//@JsonProperty(value = "games")
	@Setter(AccessLevel.NONE)
	@JsonIgnore
	private List<GameDto> gameDtos;
	@Setter(AccessLevel.NONE)
	private int winCount; // extra field
	@Setter(AccessLevel.NONE)
	@JsonIgnore
	private List<CasinoDto> casinoDtos;
	
	public PlayerDto() {
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
	@JsonIgnore
	public Player getNameConverted(String name) {
		// "Script Joe(Human|Smart) [Elf]"
		
		String[] splitName = StringUtils.split(StringUtils.remove(StringUtils.remove(name, "]"), " ["), "()");
		if (splitName.length != 3 ||
				    splitName[0].isEmpty() || splitName[1].isEmpty() || splitName[2].isEmpty()) {
			Player newPlayer = new Player();
			newPlayer.setAlias("John 'Dto' Doe");
			newPlayer.setAiLevel(AiLevel.HUMAN);
			newPlayer.setHuman(true);
			newPlayer.setAvatar(Avatar.ELF);
			return newPlayer;
		}
		
		Player newPlayer = new Player();
		newPlayer.setAlias(splitName[0]);
		newPlayer.setAiLevel(AiLevel.fromLabel(splitName[1]));
		newPlayer.setHuman(aiLevel.equalsIgnoreCase("human") ? true : false);
		newPlayer.setAvatar(Avatar.fromLabel(splitName[2]));
		return newPlayer;
	}
	
	public void setName() {
		// "Script Joe(Human|Smart) [Elf]"
		this.name = alias + "(" + WordUtils.capitalizeFully(aiLevel) + ") [" + WordUtils.capitalizeFully(avatar) + "]";
	}
	
	public void setWinCount() {
		if (gameDtos !=null) {
			this.winCount = gameDtos.size();
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
	
	public void setGameDtos(List<GameDto> gameDtos) {
		this.winCount = gameDtos == null ? 0 : gameDtos.size();
		this.gameDtos = gameDtos;
	}
	
	public List<GameDto> getGameDtos() {
		return this.gameDtos;
	}
	
	public void setCasinoDtos(List<CasinoDto> casinoDtos) {
		this.casinoDtos = casinoDtos;
	}
	
	public List<CasinoDto> getCasinoDtos() {
		return this.casinoDtos;
	}
	
}
