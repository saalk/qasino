package applyextra.commons.util;

import com.ing.api.toolkit.trust.context.ChannelContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Service that provides encrypting and decrypting of {@link String} values.
 * @deprecated use {@link SecurityEncryptionUtil}
 */
@Deprecated
@Component
@Slf4j
@Lazy
public class SecurityService {

//    @Autowired
//    private EncryptionUtil encryptionUtil;

    private String sharedKey;

    @Resource(name = "sharedSecret")
    public void setSharedKey(String sharedKey) {
        this.sharedKey = sharedKey;
    }

    /**
     * Decrypt specified cypher text.
     * <p>
     * Will throw {@link SecurityException} if the supplied input cipher text is not valid.
     *
     * @param context
     *            User session object.
     *
     * @param input
     *            The value to decrypt.
     * @return the decrypted value.
     * @deprecated use {@link SecurityEncryptionUtil#decrypt(String, Object)}
     */
    @Deprecated
    public String decrypt(final ChannelContext context, final String input) {
//        try {
            throw new UnsupportedOperationException("This operation is no longer suported within RIAF. Please implement the SecurityEncryptionUtil.java class for all encrypting matters");
//            return encryptionUtil.decrypt(input, context, sharedKey, true);
//        } catch (final Exception e) {
//            throw new WebApplicationException("Error decrypting value: " + input, Response.Status.INTERNAL_SERVER_ERROR);
//        }
    }

    /**
     * Encrypt specified value.
     * <p>
     * Will throw {@link SecurityException} if the supplied input text is not valid.
     *
     * @param context
     *            User session object.
     *
     * @param input
     *            The value to encrypt.
     * @return the encrypted value.
     * @deprecated use {@link SecurityEncryptionUtil#encrypt(String, Object)}
     */
    @Deprecated
    public String encrypt(final ChannelContext context, final String input) {
        throw new UnsupportedOperationException("This operation is no longer suported within RIAF. Please implement the SecurityEncryptionUtil.java class for all encrypting matters");
//        try {
//            return encryptionUtil.encrypt(input, context, sharedKey, true);
//        } catch (final Exception e) {
//            throw new WebApplicationException("Error encrypting value: " + input, Response.Status.INTERNAL_SERVER_ERROR);
//        }
    }

    @Deprecated
    public String encryptWithToolkit(ChannelContext channelContext, String input) {
        byte[] key = com.ing.api.toolkit.encryption.EncryptionUtil.hexToKey(channelContext.getContextSession().getSessionSharedSecret());
        return com.ing.api.toolkit.encryption.EncryptionUtil.encryptAndEncodeWithPrependedIV(input, key, true);
    }

    @Deprecated
    public String decryptWithToolkit(ChannelContext channelContext, String input) {
        byte[] key = com.ing.api.toolkit.encryption.EncryptionUtil.hexToKey(channelContext.getContextSession().getSessionSharedSecret());
        return com.ing.api.toolkit.encryption.EncryptionUtil.decodeAndDecryptWithPrependedIV(input, key, true);
    }

}
