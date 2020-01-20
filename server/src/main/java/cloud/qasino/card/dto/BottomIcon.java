package cloud.qasino.card.dto;

import cloud.qasino.card.domain.qasino.Totals;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//@JsonIdentityInfo(generator = JSOGGenerator.class)
//@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class BottomIcon {

    private Totals totals = new Totals();

    public BottomIcon() {

    }
}