package cloud.qasino.games.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
/*
  The purpose of using this Dto is to separate the internal representation of invitations data
  (e.g., in the database or business logic) from the data exposed to
  external clients or systems.
  */
public class InvitationsDto {

    // buttons
    // 1 accept or decline invitation for a game
    // 2 when accepted and playable its selected game

    // Main - 1, 2
    @JsonProperty("GameInvitations")
    private List<GameDto> gameInvitations;

}
