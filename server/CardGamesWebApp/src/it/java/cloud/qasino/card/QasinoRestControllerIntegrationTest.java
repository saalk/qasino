package cloud.qasino.card;

import cloud.qasino.card.configuration.GamesApplication;
import cloud.qasino.card.database.entity.User;
import cloud.qasino.card.database.repository.*;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

//@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = GamesApplication.class)
class QasinoRestControllerIntegrationTest {

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

    // CREATE, GET, PUT, DELETE for single entities
    // /api/users/{id} - GET, DELETE, PUT userName, email only
    // /api/games/{id} - GET, DELETE, PUT type, style, ante - rules apply!
    // /api/players/{id} - GET, DELETE, PUT sequence, PUT avatar, ailevel - rules apply


    @Test
    public void givenExistingUser_whenPatched_thenOnlyPatchedFieldsUpdated() {
//        Map<String, Boolean> communicationPreferences = new HashMap<>();
//        communicationPreferences.put("post", true);
//        communicationPreferences.put("email", true);
        User newUser = new User("userName",1, "email");
        User user = userRepository.save(newUser);
        String userId = String.valueOf(user.getUserId());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("application/json"));
        ResponseEntity<User> getResponse
                = testRestTemplate.exchange("/users/{id}",
                HttpMethod.GET,
                new HttpEntity<>("", headers),
                User.class,
                user.getUserId());

        User userResponse = getResponse.getBody();
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(userResponse.getUserId()).isEqualTo(userId);
        assertThat(userResponse.getUserName()).isEqualTo("userName");
    }

}