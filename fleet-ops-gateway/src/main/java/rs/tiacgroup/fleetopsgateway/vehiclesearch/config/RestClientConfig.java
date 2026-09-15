package rs.tiacgroup.fleetopsgateway.vehiclesearch.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    @Qualifier("freeProviderRestClient")
    public RestClient freeProviderRestClient(@Value("${provider.free.base-url}") String baseUrl) {
        return RestClient.builder().baseUrl(baseUrl).build();
    }

    @Bean
    @Qualifier("premiumProviderRestClient")
    public RestClient premiumProviderRestClient(@Value("${provider.premium.base-url}") String baseUrl) {
        return RestClient.builder().baseUrl(baseUrl).build();
    }
}