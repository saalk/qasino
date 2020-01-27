package cloud.qasino.card.orchestration;

import java.util.Objects;

/**
 * Created by CL94WQ on 21-8-2017.
 */
public class Not implements Expression {

    private Object input;

    private Not(Object input) {
        this.input = input;
    }

    public boolean equals(Object result) {
        return !input.equals(result);
    }

    public static Not not(Object input) {
        return new Not(input);
    }
    
    @Override
    public int hashCode(){
        return Objects.hashCode(this.input);
    }
}
