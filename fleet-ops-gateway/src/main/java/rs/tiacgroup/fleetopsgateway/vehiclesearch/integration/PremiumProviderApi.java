package rs.tiacgroup.fleetopsgateway.vehiclesearch.integration;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import rs.tiacgroup.fleetopsgateway.vehiclesearch.integration.model.PremiumProviderResponse;
import rs.tiacgroup.fleetopsgateway.vehiclesearch.integration.model.VehicleData;
import rs.tiacgroup.fleetopsgateway.vehiclesearch.mapper.VehicleResponseMapper;

@Component
public class PremiumProviderApi {

    private final RestClient restClient;
    private final VehicleResponseMapper vehicleResponseMapper;

    public PremiumProviderApi(@Qualifier("premiumProviderRestClient") RestClient restClient,
                              VehicleResponseMapper vehicleResponseMapper) {
        this.restClient = restClient;
        this.vehicleResponseMapper = vehicleResponseMapper;
    }

    @CircuitBreaker(name = "premiumProvider")
    public VehicleData callProvider(String vin) {
        PremiumProviderResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder.queryParam("query", vin).build())
                .retrieve()
                .body(PremiumProviderResponse.class);
        return vehicleResponseMapper.toVehicleData(response);
    }
}