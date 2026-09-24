package rs.tiacgroup.fleetopsgateway.vehiclesearch.integration;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import rs.tiacgroup.fleetopsgateway.searchhistory.entity.FuelType;
import rs.tiacgroup.fleetopsgateway.searchhistory.entity.VehicleStatus;
import rs.tiacgroup.fleetopsgateway.vehiclesearch.integration.model.PremiumProviderResponse;
import rs.tiacgroup.fleetopsgateway.vehiclesearch.integration.model.VehicleData;
import rs.tiacgroup.fleetopsgateway.vehiclesearch.mapper.VehicleResponseMapper;

import java.time.Duration;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PremiumProviderApiTest {

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

    private PremiumProviderApi premiumProviderApi;

    @BeforeEach
    void setUp() {
        premiumProviderApi = new PremiumProviderApi(restClient, vehicleResponseMapper);
    }

    @Test
    void callProvider_shouldReturnVehicleDataWhenProviderRespondsSuccessfully() {
        // given
        PremiumProviderResponse response = new PremiumProviderResponse(
                MAKE, MODEL, MODEL_YEAR, FUEL_TYPE_RAW, ENGINE, VEHICLE_STATUS_RAW);
        VehicleData vehicleData = new VehicleData(
                MAKE, MODEL, MODEL_YEAR, FUEL_TYPE, ENGINE, VEHICLE_STATUS);

        mockRestClientChain();
        when(responseSpec.body(PremiumProviderResponse.class)).thenReturn(response);
        when(vehicleResponseMapper.toVehicleData(response)).thenReturn(vehicleData);

        // when
        VehicleData result = premiumProviderApi.callProvider(VIN);

        // then
        assertThat(result).isEqualTo(vehicleData);
    }

    @Test
    void callProvider_shouldThrowHttpClientErrorExceptionWhenProviderReturns404() {
        // given
        mockRestClientChain();
        when(responseSpec.body(PremiumProviderResponse.class))
                .thenThrow(HttpClientErrorException.create(
                        HttpStatusCode.valueOf(404), "Not Found", null, null, null));

        // when / then
        assertThatThrownBy(() -> premiumProviderApi.callProvider(VIN))
                .isInstanceOf(HttpClientErrorException.NotFound.class);
    }

    @Test
    void callProvider_shouldThrowHttpServerErrorExceptionWhenProviderReturns503() {
        // given
        mockRestClientChain();
        when(responseSpec.body(PremiumProviderResponse.class))
                .thenThrow(HttpServerErrorException.create(
                        HttpStatusCode.valueOf(503), "Service Unavailable", null, null, null));

        // when / then
        assertThatThrownBy(() -> premiumProviderApi.callProvider(VIN))
                .isInstanceOf(HttpServerErrorException.class);
    }

    @Test
    void circuitBreaker_shouldOpenAfterConsecutiveFailures() {
        // given
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .slidingWindowSize(5)
                .minimumNumberOfCalls(5)
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofSeconds(15))
                .build();
        CircuitBreaker circuitBreaker = CircuitBreakerRegistry.of(config).circuitBreaker("premiumProvider");

        ProxyFactory proxyFactory = new ProxyFactory(premiumProviderApi);
        proxyFactory.addAdvice((org.aopalliance.intercept.MethodInterceptor) invocation ->
                circuitBreaker.executeCheckedSupplier(() -> invocation.proceed()));
        PremiumProviderApi proxiedApi = (PremiumProviderApi) proxyFactory.getProxy();

        mockRestClientChain();
        when(responseSpec.body(PremiumProviderResponse.class))
                .thenThrow(HttpServerErrorException.create(
                        HttpStatusCode.valueOf(503), "Service Unavailable", null, null, null));

        // when
        for (int i = 0; i < 5; i++) {
            try {
                proxiedApi.callProvider(VIN);
            } catch (Exception ignored) {
                // expected failures while building up the failure rate
            }
        }

        // then
        assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);
        assertThatThrownBy(() -> proxiedApi.callProvider(VIN))
                .isInstanceOf(CallNotPermittedException.class);
    }

    @SuppressWarnings("unchecked")
    private void mockRestClientChain() {
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }
}