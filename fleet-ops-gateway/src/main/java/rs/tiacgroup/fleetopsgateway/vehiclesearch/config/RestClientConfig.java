package rs.tiacgroup.fleetopsgateway.vehiclesearch.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(ProviderProperties.class)
public class RestClientConfig {

    @Bean
    @Qualifier("freeProviderRestClient")
    public RestClient freeProviderRestClient(ProviderProperties providerProperties) {
        return RestClient.builder().baseUrl(providerProperties.free().baseUrl()).build();
    }

    @Bean
    @Qualifier("premiumProviderRestClient")
    public RestClient premiumProviderRestClient(ProviderProperties providerProperties) {
        return RestClient.builder().baseUrl(providerProperties.premium().baseUrl()).build();
    }
}