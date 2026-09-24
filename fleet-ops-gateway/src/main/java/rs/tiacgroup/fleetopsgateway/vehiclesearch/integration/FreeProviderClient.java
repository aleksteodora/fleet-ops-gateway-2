package rs.tiacgroup.fleetopsgateway.vehiclesearch.integration;

import org.springframework.stereotype.Component;
import rs.tiacgroup.fleetopsgateway.vehiclesearch.integration.model.VehicleData;

@Component
public class FreeProviderClient extends AbstractVehicleProviderClient {

    private final FreeProviderApi freeProviderApi;

    public FreeProviderClient(FreeProviderApi freeProviderApi) {
        this.freeProviderApi = freeProviderApi;
    }

    @Override
    protected VehicleData callProvider(String vin) {
        return freeProviderApi.callProvider(vin);
    }

    @Override
    protected String getProviderName() {
        return "FREE";
    }
}