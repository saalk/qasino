package cloud.qasino.games.dto.elements;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.voodoodyne.jackson.jsog.JSOGGenerator;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//@JsonIdentityInfo(generator = JSOGGenerator.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class NavigationBarItem {

    @JsonProperty("intSequence")
    private int itemSequence;
    @JsonProperty("visible")
    private boolean itemVisible;
    @JsonProperty("name")
    private String itemName;
    @JsonProperty("stats")
    private String itemStats;

}

