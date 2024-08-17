package cloud.qasino.games.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
//@JsonIdentityInfo(generator = JSOGGenerator.class)
//@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class NavigationBarItem {

    public int sequence;
    public boolean visible;
    public String title;
    public String stat;

    @Override public String toString() {
        return "navBarItem" +
                "(sequence=" + this.sequence + ", "+
                "visible=" + this.visible + ", "+
                "title=" + this.title + ", "+
                "stat=" + this.stat + ")";
    }
}

