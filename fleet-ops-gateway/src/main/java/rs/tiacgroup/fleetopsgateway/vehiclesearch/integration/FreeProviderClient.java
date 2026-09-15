package rs.tiacgroup.fleetopsgateway.vehiclesearch.integration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import rs.tiacgroup.fleetopsgateway.vehiclesearch.integration.model.FreeProviderResponse;
import rs.tiacgroup.fleetopsgateway.vehiclesearch.integration.model.VehicleData;
import rs.tiacgroup.fleetopsgateway.vehiclesearch.mapper.VehicleResponseMapper;

@Component
public class FreeProviderClient extends AbstractVehicleProviderClient {

    private final VehicleResponseMapper vehicleResponseMapper;

    public FreeProviderClient(@Qualifier("freeProviderRestClient") RestClient restClient,
                              VehicleResponseMapper vehicleResponseMapper) {
        super(restClient);
        this.vehicleResponseMapper = vehicleResponseMapper;
    }

    @Override
    protected VehicleData callProvider(String vin) {
        FreeProviderResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder.queryParam("query", vin).build())
                .retrieve()
                .body(FreeProviderResponse.class);
        return vehicleResponseMapper.toVehicleData(response);
    }

    @Override
    protected String getProviderName() {
        return "FREE";
    }
}