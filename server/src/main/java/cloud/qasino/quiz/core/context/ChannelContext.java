package cloud.qasino.quiz.core.context;

import cloud.qasino.quiz.core.context.ChannelContextException;
import cloud.qasino.quiz.core.context.ContextSession;
import cloud.qasino.quiz.core.context.Customers;
import org.hibernate.validator.internal.metadata.facets.Validatable;

import java.util.Map;
import java.util.Optional;

public interface ChannelContext extends Validatable {
    Optional<Customers> getCustomers();

    ContextSession getContextSession();

    String getConsumerIdentification();

    String getRequestIdentification();

    String getSourceIpAddress();

    Optional<String> getXForwardFor();

    Optional<Boolean> getResourceTrace();

    Map<String, String> getContextHeadersForForwarding(String var1) throws ChannelContextException;
}
