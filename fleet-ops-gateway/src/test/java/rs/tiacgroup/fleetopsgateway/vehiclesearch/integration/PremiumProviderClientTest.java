package rs.tiacgroup.fleetopsgateway.vehiclesearch.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rs.tiacgroup.fleetopsgateway.searchhistory.entity.FuelType;
import rs.tiacgroup.fleetopsgateway.searchhistory.entity.VehicleStatus;
import rs.tiacgroup.fleetopsgateway.vehiclesearch.integration.model.VehicleData;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PremiumProviderClientTest {

    private static final String VIN = "TESTVIN0000000001";
    private static final String MAKE = "BMW";
    private static final String MODEL = "320d";
    private static final Integer MODEL_YEAR = 2020;
    private static final FuelType FUEL_TYPE = FuelType.DIESEL;
    private static final String ENGINE = "2.0";
    private static final VehicleStatus VEHICLE_STATUS = VehicleStatus.ACTIVE;

    @Mock
    private PremiumProviderApi premiumProviderApi;

    private PremiumProviderClient premiumProviderClient;

    @BeforeEach
    void setUp() {
        premiumProviderClient = new PremiumProviderClient(premiumProviderApi);
    }

    @Test
    void search_shouldReturnFoundWhenApiReturnsData() {
        // given
        VehicleData vehicleData = new VehicleData(
                MAKE, MODEL, MODEL_YEAR, FUEL_TYPE, ENGINE, VEHICLE_STATUS);
        when(premiumProviderApi.callProvider(VIN)).thenReturn(vehicleData);

        // when
        VehicleProviderResult result = premiumProviderClient.search(VIN);

        // then
        assertThat(result.outcome()).isEqualTo(VehicleProviderResult.Outcome.FOUND);
        assertThat(result.vehicleData()).isEqualTo(vehicleData);
    }

    @Test
    void search_shouldReturnNotFoundWhenApiThrowsHttpClientErrorException() {
        // given
        when(premiumProviderApi.callProvider(VIN))
                .thenThrow(org.springframework.web.client.HttpClientErrorException.NotFound.class);

        // when
        VehicleProviderResult result = premiumProviderClient.search(VIN);

        // then
        assertThat(result.outcome()).isEqualTo(VehicleProviderResult.Outcome.NOT_FOUND);
        assertThat(result.vehicleData()).isNull();
    }

    @Test
    void search_shouldReturnUnavailableWhenApiThrowsHttpServerErrorException() {
        // given
        when(premiumProviderApi.callProvider(VIN))
                .thenThrow(org.springframework.web.client.HttpServerErrorException.class);

        // when
        VehicleProviderResult result = premiumProviderClient.search(VIN);

        // then
        assertThat(result.outcome()).isEqualTo(VehicleProviderResult.Outcome.UNAVAILABLE);
        assertThat(result.vehicleData()).isNull();
    }

    @Test
    void search_shouldReturnUnavailableWhenApiThrowsCallNotPermittedException() {
        // given
        io.github.resilience4j.circuitbreaker.CircuitBreaker circuitBreaker =
                io.github.resilience4j.circuitbreaker.CircuitBreaker.ofDefaults("test");
        when(premiumProviderApi.callProvider(VIN))
                .thenThrow(io.github.resilience4j.circuitbreaker.CallNotPermittedException.createCallNotPermittedException(circuitBreaker));

        // when
        VehicleProviderResult result = premiumProviderClient.search(VIN);

        // then
        assertThat(result.outcome()).isEqualTo(VehicleProviderResult.Outcome.UNAVAILABLE);
        assertThat(result.vehicleData()).isNull();
    }
}