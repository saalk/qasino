package cloud.qasino.quiz.model.score;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.Generated;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "userId",
        "email",
        "token",
        "username",
        "userNameSequence",
        "created",
        "day",
        "week",
        "bio",
        "image",
        "securedLoan"
})
@Generated("jsonschema2pojo")
public class User {

    @JsonProperty("userId")
    private Integer userId;
    @JsonProperty("email")
    private String email;
    @JsonProperty("token")
    private String token;
    @JsonProperty("username")
    private String username;
    @JsonProperty("userNameSequence")
    private Integer userNameSequence;
    @JsonProperty("created")
    private String created;
    @JsonProperty("day")
    private Integer day;
    @JsonProperty("week")
    private String week;
    @JsonProperty("bio")
    private String bio;
    @JsonProperty("image")
    private String image;
    @JsonProperty("securedLoan")
    private Integer securedLoan;

}
