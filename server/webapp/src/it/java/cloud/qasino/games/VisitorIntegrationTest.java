package cloud.qasino.games;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class VisitorIntegrationTest extends BaseApplicationIT {

    @Test
    public void givenExistingVisitor_whenPatched_thenOnlyPatchedFieldsUpdated() {
//        Map<String, Boolean> communicationPreferences = new HashMap<>();
//        communicationPreferences.put("post", true);
//        communicationPreferences.put("email", true);

        final var visitor = createVisitorInDatabase();
        final var responseEntity = getVisitorById(visitor.getVisitorId());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        final var reponseVisitor = mapResponseToVisitorObject(responseEntity.getBody());
        assertNotNull(reponseVisitor.getCreated());

        assertEquals(visitor.getVisitorId(), reponseVisitor.getVisitorId());
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