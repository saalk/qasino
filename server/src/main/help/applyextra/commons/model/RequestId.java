package applyextra.commons.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@AllArgsConstructor
public final class RequestId {

    @JsonProperty("requestId")
    @NotNull(message = "{mandatory.requestId}")
    @Pattern(regexp = "[0-9a-fA-F]{8}\\-([0-9a-fA-F]{4}\\-){3}[0-9a-fA-F]{12}", message = "{numberFormat.requestId}")
    private String id;
}
