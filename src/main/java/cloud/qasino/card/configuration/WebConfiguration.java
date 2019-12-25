package cloud.qasino.card.configuration;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"cloud.qasino.card"})
public class WebConfiguration {

    @Bean(name = "modelmapper")
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}

