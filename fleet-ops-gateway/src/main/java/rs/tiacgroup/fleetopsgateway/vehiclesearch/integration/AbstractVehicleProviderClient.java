package rs.tiacgroup.fleetopsgateway.vehiclesearch.integration;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import rs.tiacgroup.fleetopsgateway.vehiclesearch.integration.model.VehicleData;

@Slf4j
public abstract class AbstractVehicleProviderClient implements VehicleProviderClient {

    @Override
    public VehicleProviderResult search(String vin) {
        String providerName = getProviderName();
        log.info("Calling {} provider for vin={}", providerName, vin);

        try {
            VehicleData vehicleData = callProvider(vin);
            log.info("{} provider returned data for vin={}", providerName, vin);
            return new VehicleProviderResult(VehicleProviderResult.Outcome.FOUND, vehicleData);

        } catch (HttpClientErrorException.NotFound ex) {
            log.info("{} provider found no results for vin={}", providerName, vin);
            return new VehicleProviderResult(VehicleProviderResult.Outcome.NOT_FOUND, null);

        } catch (HttpServerErrorException | ResourceAccessException ex) {
            log.warn("{} provider unavailable for vin={}: {}", providerName, vin, ex.getMessage());
            return new VehicleProviderResult(VehicleProviderResult.Outcome.UNAVAILABLE, null);

        } catch (CallNotPermittedException ex) {
            log.warn("{} provider circuit breaker is OPEN, skipping call for vin={}", providerName, vin);
            return new VehicleProviderResult(VehicleProviderResult.Outcome.UNAVAILABLE, null);
        }
    }

    protected abstract VehicleData callProvider(String vin);

    protected abstract String getProviderName();
}