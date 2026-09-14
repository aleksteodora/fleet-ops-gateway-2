package rs.tiacgroup.fleetopsgateway.searchhistory.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rs.tiacgroup.fleetopsgateway.searchhistory.entity.FuelType;
import rs.tiacgroup.fleetopsgateway.searchhistory.entity.ProviderType;
import rs.tiacgroup.fleetopsgateway.searchhistory.entity.SearchHistory;
import rs.tiacgroup.fleetopsgateway.searchhistory.entity.SearchStatus;
import rs.tiacgroup.fleetopsgateway.searchhistory.entity.VehicleStatus;
import rs.tiacgroup.fleetopsgateway.searchhistory.repository.SearchHistoryRepository;
import rs.tiacgroup.fleetopsgateway.vehiclesearch.integration.model.VehicleData;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SearchHistoryServiceTest {

    private static final Long USER_ID = 1L;
    private static final Long COMPANY_ID = 1L;
    private static final String VIN = "WBA12345678901234";
    private static final String MAKE = "BMW";
    private static final String MODEL = "320d";
    private static final Integer MODEL_YEAR = 2020;
    private static final String ENGINE = "2.0";

    @Mock
    private SearchHistoryRepository searchHistoryRepository;

    @InjectMocks
    private SearchHistoryService searchHistoryService;

    @Test
    void saveFound_shouldPersistSearchHistoryWithVehicleDataAndFoundStatus() {
        // given
        VehicleData vehicleData = new VehicleData(
                MAKE, MODEL, MODEL_YEAR, FuelType.DIESEL, ENGINE, VehicleStatus.ACTIVE);
        ArgumentCaptor<SearchHistory> captor = ArgumentCaptor.forClass(SearchHistory.class);

        // when
        searchHistoryService.saveFound(USER_ID, COMPANY_ID, VIN, ProviderType.FREE, vehicleData);

        // then
        verify(searchHistoryRepository).save(captor.capture());
        SearchHistory saved = captor.getValue();

        assertThat(saved.getUserId()).isEqualTo(USER_ID);
        assertThat(saved.getCompanyId()).isEqualTo(COMPANY_ID);
        assertThat(saved.getVin()).isEqualTo(VIN);
        assertThat(saved.getProvider()).isEqualTo(ProviderType.FREE);
        assertThat(saved.getMake()).isEqualTo(MAKE);
        assertThat(saved.getModel()).isEqualTo(MODEL);
        assertThat(saved.getModelYear()).isEqualTo(MODEL_YEAR);
        assertThat(saved.getFuelType()).isEqualTo(FuelType.DIESEL);
        assertThat(saved.getEngine()).isEqualTo(ENGINE);
        assertThat(saved.getVehicleStatus()).isEqualTo(VehicleStatus.ACTIVE);
        assertThat(saved.getSearchStatus()).isEqualTo(SearchStatus.FOUND);
    }

    @Test
    void saveNoResults_shouldPersistSearchHistoryWithNoResultsStatus() {
        // given
        ArgumentCaptor<SearchHistory> captor = ArgumentCaptor.forClass(SearchHistory.class);

        // when
        searchHistoryService.saveNoResults(USER_ID, COMPANY_ID, VIN, ProviderType.FREE);

        // then
        verify(searchHistoryRepository).save(captor.capture());
        SearchHistory saved = captor.getValue();

        assertThat(saved.getUserId()).isEqualTo(USER_ID);
        assertThat(saved.getCompanyId()).isEqualTo(COMPANY_ID);
        assertThat(saved.getVin()).isEqualTo(VIN);
        assertThat(saved.getProvider()).isEqualTo(ProviderType.FREE);
        assertThat(saved.getSearchStatus()).isEqualTo(SearchStatus.NO_RESULTS);
        assertThat(saved.getMake()).isNull();
    }

    @Test
    void saveThirdPartyDown_shouldPersistSearchHistoryWithThirdPartyDownStatus() {
        // given
        ArgumentCaptor<SearchHistory> captor = ArgumentCaptor.forClass(SearchHistory.class);

        // when
        searchHistoryService.saveThirdPartyDown(USER_ID, COMPANY_ID, VIN, ProviderType.PREMIUM);

        // then
        verify(searchHistoryRepository).save(captor.capture());
        SearchHistory saved = captor.getValue();

        assertThat(saved.getUserId()).isEqualTo(USER_ID);
        assertThat(saved.getCompanyId()).isEqualTo(COMPANY_ID);
        assertThat(saved.getVin()).isEqualTo(VIN);
        assertThat(saved.getProvider()).isEqualTo(ProviderType.PREMIUM);
        assertThat(saved.getSearchStatus()).isEqualTo(SearchStatus.THIRD_PARTY_DOWN);
        assertThat(saved.getMake()).isNull();
    }
}