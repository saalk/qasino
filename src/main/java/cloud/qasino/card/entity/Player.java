package cloud.qasino.card.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import cloud.qasino.card.entity.enums.AiLevel;
import cloud.qasino.card.entity.enums.Avatar;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * <H1>Player</H1>
 * The class variables are immutable ('private') so they can't be accessed outside class
 *
 * @author Klaas van der Meulen
 */

//@Entity is deprecated in Hibernate 4 so use JPA annotations directly in the model class

@Getter
@Setter
@Entity
@DynamicUpdate
@Table(name = "PLAYER", indexes = {@Index(name = "PLAYER_INDEX", columnList = "PLAYER_ID")})
//@Table(name = "PLAYER")
//@Relation(value = "player", collectionRelation = "players")
//@JsonIdentityInfo(generator=JSOGGenerator.class)
// - this annotation adds @Id to prevent chain loop
// - you could also use @JsonManagedReference and @JsonBackReference
public class Player implements Serializable {
    
    // 9 fields
    @Id
    @Column(name = "PLAYER_ID")
    @GeneratedValue(strategy = GenerationType.TABLE)
    ////@JsonProperty("suppliedPlayerId")
    private int playerId;

    @Column(name = "CREATED", length = 25)
    ////@JsonProperty("created")
    private String created;

    @Enumerated(EnumType.STRING)
    @Column(name = "AVATAR", nullable = false, length = 50)
    ////@JsonProperty("avatar")
    private Avatar avatar;
	
	@Column(name = "ALIAS", length = 50)
	////@JsonProperty("alias")
	private String alias;
	
    @Column(name = "HUMAN")
    @Setter(AccessLevel.NONE)
    ////@JsonProperty("human")
    private boolean human;

    @Enumerated(EnumType.STRING)
    @Column(name = "AI_LEVEL", nullable = false, length = 50)
    ////@JsonProperty("aiLevel")
    private AiLevel aiLevel;

    @Column(name = "CUBITS")
    ////@JsonProperty("cubits")
    private int cubits;
    
    @Column(name = "SECURED_LOAN")
    ////@JsonProperty("securedLoan")
    private int securedLoan;
    
    // Cascade = any change happened on this entity must cascade to the parent/child as well
    // since this is the parent Player: delete Game when Player is deleted (no other actions!)
    //@JsonIgnore
    @OneToMany(cascade= CascadeType.REMOVE)
    @JoinColumn(name = "PLAYER_ID", referencedColumnName = "PLAYER_ID", foreignKey = @ForeignKey(name = "PLAYER_ID"))
    ////@JsonProperty("gameDtos")
    @JsonIgnore
    private List<Game> games;
	
	@OneToMany(cascade= CascadeType.REMOVE)
	@JoinColumn(name = "PLAYER_ID", referencedColumnName = "PLAYER_ID", foreignKey = @ForeignKey(name = "PLAYER_ID"))
	////@JsonProperty("gameDtos")
	@JsonIgnore
	private List<Casino> casinos;
	
    public Player() {
        LocalDateTime localDateAndTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm-ssSSS-nnnnnnnnn");
        String result = localDateAndTime.format(formatter);
        this.created = result.substring(2, 20);
    }
	
	public void setHuman(boolean human) {
		this.human = human;
	}
	
	public boolean getHuman() {
		return this.human;
	}
	
}

