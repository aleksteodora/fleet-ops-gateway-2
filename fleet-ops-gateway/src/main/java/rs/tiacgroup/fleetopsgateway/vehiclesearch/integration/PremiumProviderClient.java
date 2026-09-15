package rs.tiacgroup.fleetopsgateway.vehiclesearch.integration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import rs.tiacgroup.fleetopsgateway.vehiclesearch.integration.model.PremiumProviderResponse;
import rs.tiacgroup.fleetopsgateway.vehiclesearch.integration.model.VehicleData;
import rs.tiacgroup.fleetopsgateway.vehiclesearch.mapper.VehicleResponseMapper;

@Component
public class PremiumProviderClient extends AbstractVehicleProviderClient {

    private final VehicleResponseMapper vehicleResponseMapper;

    public PremiumProviderClient(@Qualifier("premiumProviderRestClient") RestClient restClient,
                                 VehicleResponseMapper vehicleResponseMapper) {
        super(restClient);
        this.vehicleResponseMapper = vehicleResponseMapper;
    }

    @Override
    protected VehicleData callProvider(String vin) {
        PremiumProviderResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder.queryParam("query", vin).build())
                .retrieve()
                .body(PremiumProviderResponse.class);
        return vehicleResponseMapper.toVehicleData(response);
    }

    @Override
    protected String getProviderName() {
        return "PREMIUM";
    }
}