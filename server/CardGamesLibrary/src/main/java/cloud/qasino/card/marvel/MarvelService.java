package cloud.qasino.card.marvel;

import cloud.qasino.card.marvel.model.Serie;
import cloud.qasino.card.properties.MarvelProperties;
import cloud.qasino.card.util.MD5Util;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@EnableConfigurationProperties(MarvelProperties.class)
public class MarvelService {

    private static final String API_KEY = "apikey";
    private static final String HASH = "hash";
    private static final String TIMESTAMP = "ts";

    private final MarvelProperties marvelProperties;

    public MarvelService(MarvelProperties marvelProperties) {
        this.marvelProperties = marvelProperties;

//        log.info("Marvel Properties are [" + marvelProperties.toString() + "]." );
    }

    public List<java.lang.Character> getAllCharacters(int limit) throws IOException {

        Date now = new Date();
        String timestamp = String.valueOf(now.getTime());

        // generate a md5 digest of the ts parameter, your private key and your public key (e.g. md5(ts+privateKey+publicKey)
        String hash = MD5Util.hash(this.marvelProperties.getPublicKey(),
                this.marvelProperties.getPrivateKey(), timestamp);

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme(this.marvelProperties.getScheme())
                .host(this.marvelProperties.getHost())
                .path(this.marvelProperties.getCharacters().getPath())
                .queryParam(API_KEY, this.marvelProperties.getPublicKey())
                .queryParam(TIMESTAMP, timestamp)
                .queryParam(HASH, hash)
                .queryParam("limit", limit)
                .build();

//        log.info("Hash used for Marvel [" + hash + "], " +
//                "uriComponents is [" + uriComponents.toString() + "].");

        return getCharacters(uriComponents.toString());
    }

    public Optional<java.lang.Character> getCharacter(int id) throws IOException {

        Date now = new Date();
        String timestamp = String.valueOf(now.getTime());

        // generate a md5 digest of the ts parameter, your private key and your public key (e.g. md5(ts+privateKey+publicKey)
        String hash = MD5Util.hash(this.marvelProperties.getPublicKey(),
                this.marvelProperties.getPrivateKey(), timestamp);

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme(this.marvelProperties.getScheme())
                .host(this.marvelProperties.getHost())
                .path(this.marvelProperties.getCharacters().getPath())
                .pathSegment(String.valueOf(id))
                .queryParam(API_KEY, this.marvelProperties.getPublicKey())
                .queryParam(TIMESTAMP, timestamp)
                .queryParam(HASH, hash)
                .build();

        return getCharacters(uriComponents.toString()).stream().findFirst();
    }

    public List<Serie> getSeries(int id) throws IOException {

        Date now = new Date();
        String timestamp = String.valueOf(now.getTime());

        // generate a md5 digest of the ts parameter, your private key and your public key (e.g. md5(ts+privateKey+publicKey)
        String hash = MD5Util.hash(this.marvelProperties.getPublicKey(), marvelProperties.getPrivateKey(), timestamp);

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme(this.marvelProperties.getScheme())
                .host(this.marvelProperties.getHost())
                .path(this.marvelProperties.getCharacters().getPath())
                .pathSegment(id + "/series")
                .queryParam(API_KEY, this.marvelProperties.getPublicKey())
                .queryParam(TIMESTAMP, timestamp)
                .queryParam(HASH, hash)
                .build();

        return getSeries(uriComponents.toString());
    }

    private List<java.lang.Character> getCharacters(final String url) throws IOException {

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> json = restTemplate.getForEntity(url, String.class);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(Objects.requireNonNull(json.getBody(), "response body must not be null"));
        JsonNode resultsNode = rootNode.at("/data/results");
        ObjectReader reader = mapper.readerFor(new TypeReference<List<Character>>() {
        });
        return reader.readValue(resultsNode);
    }

    private List<Serie> getSeries(final String url) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> json = restTemplate.getForEntity(url, String.class);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(Objects.requireNonNull(json.getBody(), "response body must not be null"));
        JsonNode resultsNode = rootNode.at("/data/results");
        ObjectReader reader = mapper.readerFor(new TypeReference<List<Serie>>() {
        });
        return reader.readValue(resultsNode);
    }
}
