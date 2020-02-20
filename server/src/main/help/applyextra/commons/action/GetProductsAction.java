package applyextra.commons.action;

import com.ing.api.toolkit.trust.context.ChannelContext;
import applyextra.api.exception.ResourceException;
import applyextra.api.pcat.products.ProductsResourceClient;
import applyextra.api.pcat.products.value.ProductsBusinessResponse;
import applyextra.commons.event.EventOutput;
import applyextra.commons.orchestration.Action;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;


@Component
public class GetProductsAction implements Action<GetProductsAction.GetProductsActionDTO, EventOutput> {

    @Resource
    private ProductsResourceClient productsResourceClient;

    @Override
    public EventOutput perform(final GetProductsActionDTO getProductsActionDTO) {

        try {
            ProductsBusinessResponse response = productsResourceClient.execute(getProductsActionDTO.getChannelContext());
            getProductsActionDTO.setCreditcardsProduct(response);
        } catch (ResourceException e) {
            throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
        }
        return EventOutput.success();
    }

    public interface GetProductsActionDTO {

        ChannelContext getChannelContext();
        void setCreditcardsProduct(ProductsBusinessResponse productsBusinessResponse);
    }

}
