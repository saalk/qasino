package cloud.qasino.card.base;

import com.github.tomakehurst.wiremock.WireMockServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.notFound;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;


@Slf4j
@Component
public class WiremockUtil {

//    @Resource protected InvolvedPartyGetByIdServiceProperties involvedPartyGetByIdServiceProperties;

    @Resource
    protected WireMockServer wireMockServer;

    public static final String REGULAR_CUSTOMER = "6219b533-0889-469f-95f5-740fa2da637b";
    public static final String PLATINUM_CUSTOMER = "b44c34b8-b733-4e6d-810d-f223c329ec71";
    public static final String STUDENT_CUSTOMER = "8622bc82-2805-446e-89f9-bdc50e39942b";


    /**
     * Put any initialization code for each mock or stub.
     * @param wireMockServer
     */
    public void loadStubs(WireMockServer wireMockServer) {
        this.wireMockServer = wireMockServer;

        // involved party
//        final String involvedPartyPath = involvedPartyGetByIdServiceProperties.getPath().replaceAll("\\{.*}", "");
//        this.wireMockServer.stubFor(get(urlPathMatching(involvedPartyPath + REGULAR_CUSTOMER))
//                .willReturn(ok().withBodyFile("involved_party/default.json"))
//        );
//        this.wireMockServer.stubFor(get(urlPathMatching(involvedPartyPath + REGULAR_CUSTOMER))
//                .willReturn(ok().withBodyFile("involved_party/default.json"))
//        );
//        this.wireMockServer.stubFor(get(urlPathMatching(involvedPartyPath + PLATINUM_CUSTOMER))
//                .willReturn(ok().withBodyFile("involved_party/default.json"))
//        );
//        this.wireMockServer.stubFor(get(urlPathMatching(involvedPartyPath + STUDENT_CUSTOMER))
//                .willReturn(ok().withBodyFile("involved_party/default.json"))
//        );
    }
}