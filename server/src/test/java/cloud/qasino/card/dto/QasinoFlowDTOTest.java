package cloud.qasino.card.dto;


import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


class QasinoFlowDTOTest {

    @Test
    void callOrocessPathDataOK() {
        Map<String, String> tester = new HashMap<>();
        String ALIASNAME = "aliasName";
        tester.put("alias", ALIASNAME);

        QasinoFlowDTO flow = new QasinoFlowDTO();
        boolean result = flow.processPathData(tester);

        assertThat(result);
        assertThat(flow.getSuppliedAlias() == ALIASNAME );
    }

}