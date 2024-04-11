package cloud.qasino.games;

import cloud.qasino.games.GamesApplication;
import cloud.qasino.games.database.entity.Visitor;
import cloud.qasino.games.database.repository.*;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = GamesApplication.class)
@ActiveProfiles("ittest")
class QasinoRestControllerIntegrationTest {

    @Autowired
    private VisitorRepository visitorRepository;
    @Autowired
    private LeagueRepository leagueRepository;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private TurnRepository turnRepository;
    @Autowired
    private ResultsRepository resultsRepository;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Before
    public void setup() {
        testRestTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    }

    // CREATE, GET, PUT, DELETE for single entities
    // /api/visitors/{id} - GET, DELETE, PUT visitorName, email only
    // /api/games/{id} - GET, DELETE, PUT type, style, ante - rules apply!
    // /api/players/{id} - GET, DELETE, PUT sequence, PUT avatar, ailevel - rules apply


    @Test
    public void givenExistingVisitor_whenPatched_thenOnlyPatchedFieldsUpdated() {
//        Map<String, Boolean> communicationPreferences = new HashMap<>();
//        communicationPreferences.put("post", true);
//        communicationPreferences.put("email", true);
        Visitor newVisitor = new Visitor("visitorName",1, "email");
        Visitor visitor = visitorRepository.save(newVisitor);
        String visitorId = String.valueOf(visitor.getVisitorId());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("application/json"));
        ResponseEntity<Visitor> getResponse
                = testRestTemplate.exchange("/visitors/{id}",
                HttpMethod.GET,
                new HttpEntity<>("", headers),
                Visitor.class,
                visitor.getVisitorId());

        Visitor visitorResponse = getResponse.getBody();
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(visitorResponse.getVisitorId()).isEqualTo(visitorId);
        assertThat(visitorResponse.getVisitorName()).isEqualTo("visitorName");
    }

//    public void homeQasino_whenGet_thenQasinoAllReported() {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.valueOf("application/json-patch+json"));
//        String getBody = "";
//        ResponseEntity<Qasino> getResponse
//                = testRestTemplate.exchange("/home",
//                HttpMethod.GET,
//                new HttpEntity<>(getBody, headers),
//                Qasino.class);
//
//        Qasino qasinoHome = getResponse.getBody();
//        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(qasinoHome.getLeagueData().getLeagues()).isEqualTo(null);
//    }
}