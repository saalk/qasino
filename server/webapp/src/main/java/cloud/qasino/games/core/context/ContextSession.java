package cloud.qasino.games.core.context;

import org.hibernate.validator.internal.metadata.facets.Validatable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

public interface ContextSession extends Validatable {
    String getSessionIdentification();

    String getSessionSharedSecret();

    Optional<String> getDeviceIdentification();

    ChannelOfOrigin getChannelOfOrigin();

    Optional<Boolean> getSessionTraceFlag();

    Collection<String> getGroups();

    Optional<String> getChannelType();

    Optional<String> getChannelSubtype();

    Optional<String> getChannelSubtypeId();

    Integer getAuthenticationLevel();

    Long getSessionStart();

    Long getSessionEnd();

    public static enum ChannelOfOrigin {
        DIRECT_WEB,
        DIRECT_MOBILE,
        ASSISTED,
        ASSISTED_CALL,
        ASSISTED_BRANCHES,
        ASSISTED_MIDOFFICE,
        ASSISTED_SERVICEPOINT,
        OPEN_API,
        THIRD_PARTY,
        VOICE;

        private ChannelOfOrigin() {
        }

        public static boolean isAssistedChannel(ChannelOfOrigin channelOfOrigin) {
            return Arrays.asList(ASSISTED, ASSISTED_CALL, ASSISTED_BRANCHES, ASSISTED_MIDOFFICE, ASSISTED_SERVICEPOINT).contains(channelOfOrigin);
        }
    }
}
