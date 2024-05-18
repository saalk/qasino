package cloud.qasino.games.utils;

import cloud.qasino.games.action.HandleSecuredLoanAction;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Systemout {

    static int count = 1;

    public static void handleSecuredLoanActionFlow(HandleSecuredLoanAction.Dto flowDTO) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(flowDTO);
            System.out.println(count++ + " QasinoFlowDTO = " + json);
        } catch (
                JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public static void flow(HandleSecuredLoanAction.Dto flowDTO) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(flowDTO);
            System.out.println(count++ + " QasinoFlowDTO = " + json);
        } catch (
                JsonProcessingException e) {
            e.printStackTrace();
        }
    }

}

