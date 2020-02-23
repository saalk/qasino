package applyextra.commons.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SelectedCreditCard {

    @JsonProperty("cardId")
    @NotNull(message = "{mandatory.cardId}")
    @Pattern(regexp = "[0-9]{4}", message = "{numberFormat.cardId}")
    private String id;

    @Size(max = 500)
    private String encryptedAccountNumber;
}
