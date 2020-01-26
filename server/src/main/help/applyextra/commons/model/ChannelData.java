package applyextra.commons.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ing.api.toolkit.trust.context.ContextSession.ChannelOfOrigin;
import lombok.Data;

import javax.validation.constraints.Null;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChannelData {

    private final ChannelOfOrigin channelOfOrigin;
    @Null
    private final String channelType;
    @Null
    private final String channelSubType;
    @Null
    private final String channelSubTypeId;

}
