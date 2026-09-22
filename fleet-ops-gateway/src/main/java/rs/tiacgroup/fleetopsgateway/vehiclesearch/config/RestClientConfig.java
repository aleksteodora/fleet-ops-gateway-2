package rs.tiacgroup.fleetopsgateway.vehiclesearch.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
@EnableConfigurationProperties(ProviderProperties.class)
public class RestClientConfig {

    @Bean
    @Qualifier("freeProviderRestClient")
    public RestClient freeProviderRestClient(ProviderProperties providerProperties) {
        return buildRestClient(providerProperties.free());
    }

    @Bean
    @Qualifier("premiumProviderRestClient")
    public RestClient premiumProviderRestClient(ProviderProperties providerProperties) {
        return buildRestClient(providerProperties.premium());
    }

    private RestClient buildRestClient(ProviderProperties.ProviderConfig config) {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(config.connectTimeout()))
                .build();

        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(Duration.ofMillis(config.readTimeout()));

        return RestClient.builder()
                .baseUrl(config.baseUrl())
                .requestFactory(requestFactory)
                .build();
    }
}