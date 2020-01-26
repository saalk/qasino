package applyextra.presentation.resources;

import com.ing.api.toolkit.trust.context.ChannelContext;
import com.ing.api.toolkit.trust.rest.param.PeerToPeerTrustParam;
import com.ing.api.toolkit.trust.rest.param.TrustParam;
import com.ing.api.trust.jwt.p2p.PeerToPeerTrustToken;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import applyextra.controller.ApplyExtraCardController;
import applyextra.presentation.data.BeneficiaryData;
import applyextra.presentation.data.SelectData;
import applyextra.response.ApplyExtraCardCheckAndVerifyResponse;
import applyextra.response.ApplyExtraCardSelectResponse;
import applyextra.response.ApplyExtraCardSubmitResponse;
import applyextra.commons.model.RequestId;
import applyextra.commons.resource.AbstractResource;
import applyextra.commons.util.SecurityEncryptionUtil;
import applyextra.operations.model.CramCustomerRequest;
import org.apache.http.HttpStatus;
import org.joda.time.DateTimeUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
@Path("/")
@Slf4j
@Api("/api/consumer-credit-cards/requests/apply-extra-card")
public class ApplyExtraCardResource extends AbstractResource {

    @Resource
    private ApplyExtraCardController controller;
    @Resource
    private SecurityEncryptionUtil securityEncryptionUtil;


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response selectCard(@TrustParam  final ChannelContext channelContext,
                               @Valid final SelectData data) {
        log.info("########## Start of selectCard: " + LocalTime.now());

        String encryptedAccountNumber = data.getEncryptedAccountNumber();
        validate(encryptedAccountNumber);

        final String  customerId = channelContext.getCustomers().get().getActiveCustomer().getCustomerId();

        String accountNumber = null;
        if(data.getEncryptedAccountNumber() != null) {
            accountNumber = securityEncryptionUtil.decrypt(encryptedAccountNumber, channelContext);
        }

        ApplyExtraCardSelectResponse response = controller.initialize(channelContext, customerId, accountNumber, data.getCardId());
        log.info("########## End of selectCard: " + LocalTime.now());
        return Response.status(HttpStatus.SC_OK).entity(response).build();
    }


    @GET
    @Path("{id}/checkAllowed")
    @Produces(MediaType.APPLICATION_JSON)
    public Response check(@TrustParam final ChannelContext channelContext,
                          @PathParam("id") final String requestId,
                          @QueryParam("beneficiaryId") @DefaultValue("") final String beneficiaryId) {
        validate(new RequestId(requestId));
        validate(new BeneficiaryData(beneficiaryId));

        final String decryptedId = securityEncryptionUtil.decrypt(beneficiaryId, channelContext);

        final ApplyExtraCardCheckAndVerifyResponse response = controller.check(channelContext, requestId, decryptedId);
        return Response.status(HttpStatus.SC_OK).entity(response).build();
    }

    @POST
    @Path("{id}/verify")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response verify (@TrustParam final ChannelContext channelContext,
                            @PathParam("id") final String requestId,
                            @Valid final BeneficiaryData beneficiary) {
        validate(new RequestId(requestId));
        validate(beneficiary);

        final String decryptedId = securityEncryptionUtil.decrypt(beneficiary.getBeneficiaryId(), channelContext);

        final ApplyExtraCardCheckAndVerifyResponse response = controller.verify(channelContext, requestId, decryptedId);
        return Response.status(HttpStatus.SC_OK).entity(response).build();
    }


    @POST
    @Path("{id}/submit")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response submit (@TrustParam final ChannelContext channelContext,
                            @PathParam("id") final String requestId) {
        log.debug("Enter Submit endpoint");
        validate(new RequestId(requestId));

        final ApplyExtraCardSubmitResponse response = controller.submit(channelContext, requestId);
        return Response.status(HttpStatus.SC_OK).entity(response).build();
    }


    @POST
    @Path("{id}/reset")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response reset (@TrustParam final ChannelContext channelContext,
                           @PathParam("id") final String requestId) {

        validate(new RequestId(requestId));

        controller.reset(channelContext, requestId);

        return Response.status(HttpStatus.SC_OK).build();
    }

    @POST
    @Path("authorize")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response authorize(@PeerToPeerTrustParam final PeerToPeerTrustToken token,
                              @RequestBody final CramCustomerRequest customerRequest) {
        log.debug("Enter Authorize endpoint");

        validate(customerRequest);
        validate(new RequestId(customerRequest.getCustomerRequest().getExternalReference().getId()));

        if (token == null || !token.isVerified()) {
            log.warn("Unauthorized request " + customerRequest.getCustomerRequest().getExternalReference().getId()
                    + " entering authorize endpoint. Returning http: 403");
            return Response.status(HttpStatus.SC_FORBIDDEN).build();
        }

        controller.authorize(customerRequest);

        return Response.status(HttpStatus.SC_OK).build();
    }

}
