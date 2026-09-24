package rs.tiacgroup.fleetopsgateway.vehiclesearch.integration;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import rs.tiacgroup.fleetopsgateway.vehiclesearch.integration.model.FreeProviderResponse;
import rs.tiacgroup.fleetopsgateway.vehiclesearch.integration.model.VehicleData;
import rs.tiacgroup.fleetopsgateway.vehiclesearch.mapper.VehicleResponseMapper;

@Component
public class FreeProviderApi {

    private final RestClient restClient;
    private final VehicleResponseMapper vehicleResponseMapper;

    public FreeProviderApi(@Qualifier("freeProviderRestClient") RestClient restClient,
                           VehicleResponseMapper vehicleResponseMapper) {
        this.restClient = restClient;
        this.vehicleResponseMapper = vehicleResponseMapper;
    }

    @CircuitBreaker(name = "freeProvider")
    public VehicleData callProvider(String vin) {
        FreeProviderResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder.queryParam("query", vin).build())
                .retrieve()
                .body(FreeProviderResponse.class);
        return vehicleResponseMapper.toVehicleData(response);
    }
}