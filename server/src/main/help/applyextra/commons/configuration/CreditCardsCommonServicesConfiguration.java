package applyextra.commons.configuration;

import nl.ing.riaf.core.configuration.BasicConfiguration;
import nl.ing.riaf.ix.configuration.IXCoreConfiguration;
import nl.ing.sc.customerrequest.createcustomerrequest1.configuration.CreateCustomerRequestConfiguration;
import nl.ing.sc.customerrequest.setcustomerrequeststatus1.configuration.SetCustomerRequestStatusConfiguration;
import nl.ing.sc.customerrequest.updatecustomerrequest1.configuration.UpdateCustomerRequestConfiguration;
import nl.ing.serviceclient.lecca.configuration.RetrieveCreditCardListConfiguration;
import nl.ing.serviceclient.lecca.service.RetrieveCreditCardListService;
import nl.ing.serviceclient.sia.applyrepricing.configuration.ApplyRepricingWrapperWithFreemarkerConfiguration;
import nl.ing.serviceclient.sia.inquireaccount2.configuration.InquireAccount2WrapperWithFreemarkerConfiguration;
import nl.ing.serviceclient.sia.maintainfullaccount.configuration.MaintainFullAccountConfiguration;
import nl.ing.serviceclient.sia.maintainfullaccount.configuration.MaintainFullAccountFreemarkerConfiguration;
import nl.ing.serviceclient.verifycreditcardoperation.configuration.VerifyCreditCardOperationConfiguration;
import nl.ing.serviceclient.verifycreditcardoperation.service.VerifyCreditCardOperationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;

@Lazy
@Configuration
@Import({BasicConfiguration.class, IXCoreConfiguration.class,
        RetrieveCreditCardListConfiguration.class,
        VerifyCreditCardOperationConfiguration.class,
        CreateCustomerRequestConfiguration.class,
        UpdateCustomerRequestConfiguration.class,
        SetCustomerRequestStatusConfiguration.class,
        InquireAccount2WrapperWithFreemarkerConfiguration.class,
        ApplyRepricingWrapperWithFreemarkerConfiguration.class,
        MaintainFullAccountConfiguration.class,
        MaintainFullAccountFreemarkerConfiguration.class
})
public class CreditCardsCommonServicesConfiguration {

    @Bean
    public VerifyCreditCardOperationService getVerifyCreditCardOperationService() {
        return new VerifyCreditCardOperationService();
    }

    @Bean
    public RetrieveCreditCardListService getRetrieveCreditCardListService() {
        return new RetrieveCreditCardListService();
    }
}
