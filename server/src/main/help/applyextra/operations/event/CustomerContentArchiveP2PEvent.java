package applyextra.operations.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ing.api.toolkit.http.japi.RichHttpRequestBuilder;
import com.ing.api.toolkit.integration.discovery.Resolver;
import com.twitter.finagle.Service;
import com.twitter.finagle.http.Request;
import com.twitter.util.Await;
import com.twitter.util.Duration;
import lombok.extern.slf4j.Slf4j;
import applyextra.commons.activity.ActivityException;
import applyextra.commons.event.AbstractEvent;
import applyextra.commons.event.EventOutput;
import applyextra.customercontent.configuration.CustomerContentP2PConfiguration;
import applyextra.customercontent.model.DocumentVO;
import org.apache.commons.lang3.StringUtils;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Lazy
@Slf4j
@Component
public class CustomerContentArchiveP2PEvent extends AbstractEvent {

    private static final long SERVICE_TIMEOUT = 5000;

    @Resource
    private Service<Request, DocumentVO> customerContentAPIP2PClient;

    @Resource
    private String customerContentApiP2PResource;

    @Resource
    private Resolver serviceResolver;

    @Override
    public EventOutput execution(final Object... eventOutput) {
        log.debug("Executing CustomerContentArchiveP2PEvent...");
        try {
            final CustomerContentEventDTO flowDTO = (CustomerContentEventDTO) eventOutput[0];
            final Request customerContentRequest = createPostRequest(flowDTO.getDocumentVO());
            final DocumentVO documentVO = callCustomerContentAPI(customerContentRequest);
            log.debug("Reponse Received from CustomerContentArchiveP2PEvent :" + new ObjectMapper().writeValueAsString(documentVO));
        } catch (final Exception exception) {
            log.error("Exception while executing CustomerContentArchiveP2PEvent: ", exception);
            throw new ActivityException(CustomerContentP2PConfiguration.SERVICE_NAME, "call to CustomerContentArchiveP2PEvent failed", exception);
        }
        return EventOutput.success();
    }

    private Request createPostRequest(final DocumentVO documentVO) throws Exception {
        return new RichHttpRequestBuilder().withMethod(HttpMethod.POST)
                .withJsonContent(documentVO)
                .withPath(StringUtils.appendIfMissing(customerContentApiP2PResource, "/", new CharSequence[0]) + documentVO.getDocumentId())
                .build();
    }

    private DocumentVO callCustomerContentAPI(final Request request) throws Exception {
        return Await.result(customerContentAPIP2PClient.apply(request), Duration.fromMilliseconds(SERVICE_TIMEOUT));
    }


    public interface CustomerContentEventDTO {
        DocumentVO getDocumentVO();
    }

}
