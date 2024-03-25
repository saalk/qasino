package cloud.qasino.card.controller;

import cloud.qasino.card.configuration.GamesApplication;
import cloud.qasino.card.repository.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
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

//    @Test
//    public void givenExistingCustomer_whenPatched_thenOnlyPatchedFieldsUpdated() {
//        Map<String, Boolean> communicationPreferences = new HashMap<>();
//        communicationPreferences.put("post", true);
//        communicationPreferences.put("email", true);
//        Customer newCustomer = new Customer("001-555-1234", Arrays.asList("Milk", "Eggs"),
//                communicationPreferences);
//        Customer customer = customerService.createCustomer(newCustomer);
//
//
//        String patchBody = "[ { \"op\": \"replace\", \"path\": \"/telephone\", \"value\": \"001-555-5678\" },\n"
//                + "{\"op\": \"add\", \"path\": \"/favorites/0\", \"value\": \"Bread\" }]";
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.valueOf("application/json-patch+json"));
//        ResponseEntity<Customer> patchResponse
//                = testRestTemplate.exchange("/customers/{id}",
//                HttpMethod.PATCH,
//                new HttpEntity<>(patchBody, headers),
//                Customer.class,
//                customer.getId());
//
//        Customer customerPatched = patchResponse.getBody();
//        assertThat(patchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(customerPatched.getId()).isEqualTo(customer.getId());
//        assertThat(customerPatched.getTelephone()).isEqualTo("001-555-5678");
//        assertThat(customerPatched.getCommunicationPreferences().get("post")).isTrue();
//        assertThat(customerPatched.getCommunicationPreferences().get("email")).isTrue();
//        assertThat(customerPatched.getFavorites()).containsExactly("Bread", "Milk", "Eggs");
//    }

}