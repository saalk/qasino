package cloud.qasino.card.dto;


import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


class QasinoFlowDTOTest {

    @Test
    void processInput() {
    }

    @Test
    void processHeader() {
    }

    @Test
    void processPathData() {
        Map<String, String> tester = new HashMap<>();
        tester.put("alias", "aliasName");

        QasinoFlowDTO flow = new QasinoFlowDTO();
        boolean result = flow.processPathData(tester);

        assertThat(result);
    }

    @Test
    void processParamData() {
    }
}