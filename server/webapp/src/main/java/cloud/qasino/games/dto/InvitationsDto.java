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
    // 1 accept or decline invitation for other game
    // 2 accept or decline invitation for your current game
    // 3 when accepted and playable and no current game its selected game

    // Main - 1, 2
    private List<GameShortDto> gameInvitations;

}

