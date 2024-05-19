package cloud.qasino.games;

import cloud.qasino.games.database.security.Visitor;
import cloud.qasino.games.database.security.VisitorRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.TestInstance;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import jakarta.annotation.Resource;
import java.util.Optional;

import static cloud.qasino.games.config.Constants.BASE_PATH;
import static cloud.qasino.games.config.Constants.ENDPOINT_VISITOR;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public class BaseApplicationIT extends AbstractBaseApplicationIT {

    @Resource protected VisitorRepository visitorRepository;
    @Resource protected ObjectMapper objectMapper;

    public ResponseEntity<String> getVisitorById(final long visitorId) {
        return this.callEndpoint(HttpMethod.GET, BASE_PATH + ENDPOINT_VISITOR + "/" + visitorId, "",
                visitorId
//                ,Collections.singletonMap("visitorName", "name")
        );
    }

    @SneakyThrows
    public Visitor mapResponseToVisitorObject(String body){
        return objectMapper.readValue(body,Visitor.class);
    }

    public Visitor createVisitorInDatabase() {

        Visitor newVisitor = new Visitor.Builder()
                .withUsername("Julie")
                .withPassword("Julie")
                .withEmail("email")
                .withAlias("visitorName")
                .withAliasSequence(1)
                .build();

        return visitorRepository.save(newVisitor);
    }
    public Optional<Visitor> fetchVisitorFromDatabase(long visitorId) {
        return visitorRepository.findVisitorByVisitorId(visitorId);
    }
}