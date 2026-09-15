package rs.tiacgroup.fleetopsgateway.vehiclesearch.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rs.tiacgroup.fleetopsgateway.searchhistory.entity.FuelType;
import rs.tiacgroup.fleetopsgateway.searchhistory.entity.ProviderType;
import rs.tiacgroup.fleetopsgateway.searchhistory.entity.SearchStatus;
import rs.tiacgroup.fleetopsgateway.searchhistory.entity.VehicleStatus;
import rs.tiacgroup.fleetopsgateway.searchhistory.service.SearchHistoryService;
import rs.tiacgroup.fleetopsgateway.vehiclesearch.integration.FreeProviderClient;
import rs.tiacgroup.fleetopsgateway.vehiclesearch.integration.PremiumProviderClient;
import rs.tiacgroup.fleetopsgateway.vehiclesearch.integration.VehicleProviderResult;
import rs.tiacgroup.fleetopsgateway.vehiclesearch.integration.model.VehicleData;
import rs.tiacgroup.fleetopsgateway.vehiclesearch.model.VehicleSearchResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VehicleSearchServiceTest {

    private static final String VIN = "TESTVIN0000000001";
    private static final Long USER_ID = 1L;
    private static final Long COMPANY_ID = 1L;

    private static final VehicleData VEHICLE_DATA = new VehicleData(
            "BMW", "320d", 2020, FuelType.DIESEL, "2.0", VehicleStatus.ACTIVE);

    @Mock
    private FreeProviderClient freeProviderClient;

    @Mock
    private PremiumProviderClient premiumProviderClient;

    @Mock
    private SearchHistoryService searchHistoryService;

    @InjectMocks
    private VehicleSearchService vehicleSearchService;

    @Test
    void search_shouldReturnFoundAndNotCallPremiumWhenFreeSucceeds() {
        // given
        when(freeProviderClient.search(VIN)).thenReturn(
                new VehicleProviderResult(VehicleProviderResult.Outcome.FOUND, VEHICLE_DATA));

        // when
        VehicleSearchResult result = vehicleSearchService.search(VIN, USER_ID, COMPANY_ID);

        // then
        assertThat(result.status()).isEqualTo(SearchStatus.FOUND);
        assertThat(result.vehicleData()).isEqualTo(VEHICLE_DATA);
        verify(searchHistoryService).saveFound(USER_ID, COMPANY_ID, VIN, ProviderType.FREE, VEHICLE_DATA);
        verify(premiumProviderClient, never()).search(VIN);
    }

    @Test
    void search_shouldFallBackToPremiumAndReturnFoundWhenFreeNotFoundButPremiumSucceeds() {
        // given
        when(freeProviderClient.search(VIN)).thenReturn(
                new VehicleProviderResult(VehicleProviderResult.Outcome.NOT_FOUND, null));
        when(premiumProviderClient.search(VIN)).thenReturn(
                new VehicleProviderResult(VehicleProviderResult.Outcome.FOUND, VEHICLE_DATA));

        // when
        VehicleSearchResult result = vehicleSearchService.search(VIN, USER_ID, COMPANY_ID);

        // then
        assertThat(result.status()).isEqualTo(SearchStatus.FOUND);
        assertThat(result.vehicleData()).isEqualTo(VEHICLE_DATA);
        verify(searchHistoryService).saveFound(USER_ID, COMPANY_ID, VIN, ProviderType.PREMIUM, VEHICLE_DATA);
    }

    @Test
    void search_shouldFallBackToPremiumAndReturnFoundWhenFreeUnavailableButPremiumSucceeds() {
        // given
        when(freeProviderClient.search(VIN)).thenReturn(
                new VehicleProviderResult(VehicleProviderResult.Outcome.UNAVAILABLE, null));
        when(premiumProviderClient.search(VIN)).thenReturn(
                new VehicleProviderResult(VehicleProviderResult.Outcome.FOUND, VEHICLE_DATA));

        // when
        VehicleSearchResult result = vehicleSearchService.search(VIN, USER_ID, COMPANY_ID);

        // then
        assertThat(result.status()).isEqualTo(SearchStatus.FOUND);
        assertThat(result.vehicleData()).isEqualTo(VEHICLE_DATA);
        verify(searchHistoryService).saveFound(USER_ID, COMPANY_ID, VIN, ProviderType.PREMIUM, VEHICLE_DATA);
    }

    @Test
    void search_shouldReturnNoResultsWhenBothProvidersFindNothing() {
        // given
        when(freeProviderClient.search(VIN)).thenReturn(
                new VehicleProviderResult(VehicleProviderResult.Outcome.NOT_FOUND, null));
        when(premiumProviderClient.search(VIN)).thenReturn(
                new VehicleProviderResult(VehicleProviderResult.Outcome.NOT_FOUND, null));

        // when
        VehicleSearchResult result = vehicleSearchService.search(VIN, USER_ID, COMPANY_ID);

        // then
        assertThat(result.status()).isEqualTo(SearchStatus.NO_RESULTS);
        assertThat(result.vehicleData()).isNull();
        verify(searchHistoryService).saveNoResults(USER_ID, COMPANY_ID, VIN, ProviderType.PREMIUM);
    }

    @Test
    void search_shouldReturnThirdPartyDownWhenBothProvidersUnavailable() {
        // given
        when(freeProviderClient.search(VIN)).thenReturn(
                new VehicleProviderResult(VehicleProviderResult.Outcome.UNAVAILABLE, null));
        when(premiumProviderClient.search(VIN)).thenReturn(
                new VehicleProviderResult(VehicleProviderResult.Outcome.UNAVAILABLE, null));

        // when
        VehicleSearchResult result = vehicleSearchService.search(VIN, USER_ID, COMPANY_ID);

        // then
        assertThat(result.status()).isEqualTo(SearchStatus.THIRD_PARTY_DOWN);
        assertThat(result.vehicleData()).isNull();
        verify(searchHistoryService).saveThirdPartyDown(USER_ID, COMPANY_ID, VIN, ProviderType.PREMIUM);
    }
}