package applyextra.presentation.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SelectData {

    @JsonProperty("encryptedAccountNumber")
    //@NotNull(message = "{mandatory.accountnumber}") will need it for listaccounts
    @Size(max = 10000)
    private String encryptedAccountNumber;

    @JsonProperty("cardId")
    @NotNull(message = "{mandatory.cardId}")
    @Pattern(regexp = "[0-9]{4}", message = "{numberFormat.cardId}")
    private String cardId;
}
