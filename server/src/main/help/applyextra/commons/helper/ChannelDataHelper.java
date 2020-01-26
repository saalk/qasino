package applyextra.commons.helper;

import com.ing.api.toolkit.trust.context.ChannelContext;
import com.ing.api.toolkit.trust.context.ContextSession;
import applyextra.commons.model.ChannelData;


public class ChannelDataHelper {

    private ChannelDataHelper() {
    }

    public static ChannelData makeChannelData(ChannelContext channelContext) {
        if (channelContext != null && channelContext.getContextSession() != null) {
            ContextSession contextSession = channelContext.getContextSession();
            String channelType = contextSession.getChannelType().orElse(null);
            String channelSubType = contextSession.getChannelSubtype().orElse(null);
            String channelSubTypeId = contextSession.getChannelSubtypeId().orElse(null);
            return new ChannelData(contextSession.getChannelOfOrigin(), channelType, channelSubType, channelSubTypeId);
        }
        return null;
    }

}
