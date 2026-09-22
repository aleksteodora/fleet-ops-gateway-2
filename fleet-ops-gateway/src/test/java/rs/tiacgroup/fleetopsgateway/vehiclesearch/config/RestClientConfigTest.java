package rs.tiacgroup.fleetopsgateway.vehiclesearch.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RestClientConfigTest {

    @Test
    void buildRestClient_shouldThrowResourceAccessExceptionWhenReadTimeoutExceeded() {
        // given
        RestClientConfig config = new RestClientConfig();
        ProviderProperties.ProviderConfig providerConfig =
                new ProviderProperties.ProviderConfig("http://localhost:9999", 100, 1);
        ProviderProperties providerProperties = new ProviderProperties(providerConfig, providerConfig);

        RestClient restClient = config.freeProviderRestClient(providerProperties);

        // when / then
        assertThatThrownBy(() -> restClient.get().uri("/anything").retrieve().body(String.class))
                .isInstanceOf(ResourceAccessException.class);
    }
}