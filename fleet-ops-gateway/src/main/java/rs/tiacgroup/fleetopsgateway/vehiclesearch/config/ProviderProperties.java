package rs.tiacgroup.fleetopsgateway.vehiclesearch.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "provider")
public record ProviderProperties(
        ProviderConfig free,
        ProviderConfig premium
) {
    public record ProviderConfig(String baseUrl) {
    }
}