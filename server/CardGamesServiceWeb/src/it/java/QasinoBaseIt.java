import cloud.qasino.card.configuration.GamesApplication;
import cloud.qasino.card.dto.Qasino;
import cloud.qasino.card.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@ContextConfiguration(classes = {CreditCardsTestRequestDatabaseAutoConfiguration.class})
//@EnableAutoConfiguration(exclude = CreditCardsRequestDatabaseAutoConfiguration.class)
@Slf4j
public class QasinoBaseIt {

    @Autowired
    private UserRepository userRepository;
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


    public void homeQasino_whenGet_thenQasinoAllReported() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("application/json-patch+json"));
        String getBody = "";
        ResponseEntity<Qasino> getResponse
                = testRestTemplate.exchange("/home",
                HttpMethod.GET,
                new HttpEntity<>(getBody, headers),
                Qasino.class);

        Qasino qasinoHome = getResponse.getBody();
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(qasinoHome.getLeagueData().getLeagues()).isEqualTo(null);
    }

}
