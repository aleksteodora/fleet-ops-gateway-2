package rs.tiacgroup.fleetopsgateway.vehiclesearch.integration;

import org.springframework.stereotype.Component;
import rs.tiacgroup.fleetopsgateway.vehiclesearch.integration.model.VehicleData;

@Component
public class PremiumProviderClient extends AbstractVehicleProviderClient {

    private final PremiumProviderApi premiumProviderApi;

    public PremiumProviderClient(PremiumProviderApi premiumProviderApi) {
        this.premiumProviderApi = premiumProviderApi;
    }

    @Override
    protected VehicleData callProvider(String vin) {
        return premiumProviderApi.callProvider(vin);
    }

    @Override
    protected String getProviderName() {
        return "PREMIUM";
    }
}