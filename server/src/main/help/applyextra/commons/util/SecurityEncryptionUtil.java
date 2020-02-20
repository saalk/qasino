package applyextra.commons.util;

import com.ing.api.toolkit.trust.context.ChannelContext;
import com.ing.api.trust.jwt.p2p.PeerToPeerTrustToken;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Utility class to encrypt/decrypt
 */
@Component
public class SecurityEncryptionUtil {

    private SecurityEncryptionUtil() {
        //
    }

    /**
     * Encrypt message given in the input
     *
     * @param input  plain text
     * @param object accepts valid {@link Optional}, {@link String}, {@link ChannelContext} or {@link PeerToPeerTrustToken}
     * @return dynamic encrypted String
     */
    public String encrypt(String input, Object object) {
        if (object instanceof ChannelContext) {
            return encrypt(input, ((ChannelContext) object));
        } else if (object instanceof String) {
            return encrypt(input, ((String) object));
        } else if (object instanceof PeerToPeerTrustToken) {
            return encrypt(input, ((PeerToPeerTrustToken) object));
        } else if (object instanceof Optional) {
            if (((Optional) object).isPresent()) {
                return encrypt(input, ((Optional) object).get());
            }
        }
        throw new IllegalArgumentException("Use an Optional, String, ChannelContext or PeerToPeerTrustToken");
    }

    /**
     * Decrypt message given in the input
     *
     * @param input  encrypted text
     * @param object accepts valid {@link Optional}, {@link String}, {@link ChannelContext} or {@link PeerToPeerTrustToken}
     * @return plain text String
     */
    public String decrypt(String input, Object object) {
        if (object instanceof ChannelContext) {
            return decrypt(input, ((ChannelContext) object));
        } else if (object instanceof String) {
            return decrypt(input, ((String) object));
        } else if (object instanceof PeerToPeerTrustToken) {
            return decrypt(input, ((PeerToPeerTrustToken) object));
        } else if (object instanceof Optional) {
            if (((Optional) object).isPresent()) {
                return decrypt(input, ((Optional) object).get());
            }
        }
        throw new IllegalArgumentException("Use an Optional, String, ChannelContext or PeerToPeerTrustToken");
    }

    private static String encrypt(String input, ChannelContext channelContext) {
        return encrypt(input, channelContext.getContextSession().getSessionSharedSecret());
    }

    private static String encrypt(String input, PeerToPeerTrustToken token) {
        return encrypt(input, token.getClaimsSet().getSharedKey());
    }

    private static String encrypt(String input, String sharedSecret) {
        byte[] key = com.ing.api.toolkit.encryption.EncryptionUtil.hexToKey(sharedSecret);
        return com.ing.api.toolkit.encryption.EncryptionUtil.encryptAndEncodeWithPrependedIV(input, key, true);
    }

    private static String decrypt(String input, ChannelContext channelContext) {
        return decrypt(input, channelContext.getContextSession().getSessionSharedSecret());
    }

    private static String decrypt(String input, PeerToPeerTrustToken token) {
        return decrypt(input, token.getClaimsSet().getSharedKey());
    }

    private static String decrypt(String input, String sharedSecret) {
        byte[] key = com.ing.api.toolkit.encryption.EncryptionUtil.hexToKey(sharedSecret);
        return com.ing.api.toolkit.encryption.EncryptionUtil.decodeAndDecryptWithPrependedIV(input, key, true);
    }

}
