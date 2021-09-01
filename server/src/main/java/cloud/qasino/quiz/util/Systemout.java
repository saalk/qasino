package cloud.qasino.quiz.util;

import cloud.qasino.quiz.action.HandleSecuredLoanAction;
import cloud.qasino.quiz.dto.QasinoFlowDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Systemout {

    static int count = 1;

    public static void handleSecuredLoanActionFlow(HandleSecuredLoanAction.HandleSecuredLoanActionDTO flowDTO) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(flowDTO);
            System.out.println(count++ + " QasinoFlowDTO = " + json);
        } catch (
                JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public static void flow(HandleSecuredLoanAction.HandleSecuredLoanActionDTO flowDTO) {
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

