package applyextra.operations.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by tw83wj on 5-8-15.
 */
@Getter
@Setter
public class StateMachineDTO {
    private String initialState;
    private List<String> triggers;
    private List<String> states;
}