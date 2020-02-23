package applyextra.presentation.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BeneficiaryData {

    @JsonProperty("beneficiaryId")
    @NotNull(message = "{mandatory.beneficiaryId}")
    @Size(max = 10000)
    private String beneficiaryId;
}
