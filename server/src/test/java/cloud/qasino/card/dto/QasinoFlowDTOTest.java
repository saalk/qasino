package cloud.qasino.card.dto;


import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import static org.mockito.Mockito.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class QasinoFlowDTOTest {

    @Mock
    ServletUriComponentsBuilder servletUriComponentsBuilder;

    @Mock
    QasinoFlowDTO flow;

    @Test
    void callOrocessPathDataOK() throws URISyntaxException {

        //private boolean requestingToRepay = false;
        //private boolean offeringShipForPawn = false;

        // for servletUriComponentsBuilder.fromCurrentRequest()
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        Map<String, String> tester = new HashMap<>();
        tester.put("repayloan", "true");

        QasinoFlowDTO flow = new QasinoFlowDTO();
        flow.setParamData(tester);
        boolean result = flow.validateInput();

        assertThat(result);
        assertThat(flow.isRequestingToRepay() == true );
    }

}