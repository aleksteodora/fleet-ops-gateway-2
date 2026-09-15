package rs.tiacgroup.fleetopsgateway.vehiclesearch.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import rs.tiacgroup.fleetopsgateway.searchhistory.entity.FuelType;
import rs.tiacgroup.fleetopsgateway.searchhistory.entity.VehicleStatus;
import rs.tiacgroup.fleetopsgateway.vehiclesearch.integration.model.PremiumProviderResponse;
import rs.tiacgroup.fleetopsgateway.vehiclesearch.integration.model.VehicleData;
import rs.tiacgroup.fleetopsgateway.vehiclesearch.mapper.VehicleResponseMapper;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PremiumProviderClientTest {

    private static final String VIN = "TESTVIN0000000001";
    private static final String MAKE = "BMW";
    private static final String MODEL = "320d";
    private static final Integer MODEL_YEAR = 2020;
    private static final String FUEL_TYPE_RAW = "DIESEL";
    private static final FuelType FUEL_TYPE = FuelType.DIESEL;
    private static final String ENGINE = "2.0";
    private static final String VEHICLE_STATUS_RAW = "ACTIVE";
    private static final VehicleStatus VEHICLE_STATUS = VehicleStatus.ACTIVE;

    @Mock
    private VehicleResponseMapper vehicleResponseMapper;

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private RestClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    private PremiumProviderClient premiumProviderClient;

    @BeforeEach
    void setUp() {
        premiumProviderClient = new PremiumProviderClient(restClient, vehicleResponseMapper);
    }

    @Test
    void search_shouldReturnFoundWhenProviderRespondsSuccessfully() {
        // given
        PremiumProviderResponse response = new PremiumProviderResponse(
                MAKE, MODEL, MODEL_YEAR, FUEL_TYPE_RAW, ENGINE, VEHICLE_STATUS_RAW);
        VehicleData vehicleData = new VehicleData(
                MAKE, MODEL, MODEL_YEAR, FUEL_TYPE, ENGINE, VEHICLE_STATUS);

        mockRestClientChain();
        when(responseSpec.body(PremiumProviderResponse.class)).thenReturn(response);
        when(vehicleResponseMapper.toVehicleData(response)).thenReturn(vehicleData);

        // when
        VehicleProviderResult result = premiumProviderClient.search(VIN);

        // then
        assertThat(result.outcome()).isEqualTo(VehicleProviderResult.Outcome.FOUND);
        assertThat(result.vehicleData()).isEqualTo(vehicleData);
    }

    @Test
    void search_shouldReturnNotFoundWhenProviderReturns404() {
        // given
        mockRestClientChain();
        when(responseSpec.body(PremiumProviderResponse.class))
                .thenThrow(HttpClientErrorException.create(
                        HttpStatusCode.valueOf(404), "Not Found", null, null, null));

        // when
        VehicleProviderResult result = premiumProviderClient.search(VIN);

        // then
        assertThat(result.outcome()).isEqualTo(VehicleProviderResult.Outcome.NOT_FOUND);
        assertThat(result.vehicleData()).isNull();
    }

    @Test
    void search_shouldReturnUnavailableWhenProviderReturns503() {
        // given
        mockRestClientChain();
        when(responseSpec.body(PremiumProviderResponse.class))
                .thenThrow(HttpServerErrorException.create(
                        HttpStatusCode.valueOf(503), "Service Unavailable", null, null, null));

        // when
        VehicleProviderResult result = premiumProviderClient.search(VIN);

        // then
        assertThat(result.outcome()).isEqualTo(VehicleProviderResult.Outcome.UNAVAILABLE);
        assertThat(result.vehicleData()).isNull();
    }

    @SuppressWarnings("unchecked")
    private void mockRestClientChain() {
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }
}