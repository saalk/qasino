package cloud.qasino.quiz.domain.marvel;

import cloud.qasino.quiz.domain.marvel.Resource;
import cloud.qasino.quiz.domain.marvel.Thumbnail;
import cloud.qasino.quiz.domain.marvel.Url;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Character {
    private int id;
    private String name;
    private String description;
    private String modified;
    private Thumbnail thumbnail;
    private String resourceURI;
    private cloud.qasino.quiz.domain.marvel.Resource comics;
    private cloud.qasino.quiz.domain.marvel.Resource series;
    private cloud.qasino.quiz.domain.marvel.Resource stories;
    private Resource events;
    private List<Url> urls;
}
