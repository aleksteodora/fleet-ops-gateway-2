package rs.tiacgroup.fleetopsgateway.searchhistory.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import rs.tiacgroup.fleetopsgateway.searchhistory.entity.*;
import rs.tiacgroup.fleetopsgateway.searchhistory.repository.SearchHistoryRepository;
import rs.tiacgroup.fleetopsgateway.vehiclesearch.integration.model.VehicleData;

@Service
@Slf4j
public class SearchHistoryService {

    private final SearchHistoryRepository searchHistoryRepository;

    public SearchHistoryService(SearchHistoryRepository searchHistoryRepository) {
        this.searchHistoryRepository = searchHistoryRepository;
    }

    public void saveFound(Long userId, Long companyId, String vin, ProviderType provider, VehicleData vehicleData) {
        log.info("Saving FOUND search history for vin={} provider={}", vin, provider);

        SearchHistory searchHistory = new SearchHistory(
                userId,
                companyId,
                vin,
                provider,
                vehicleData.make(),
                vehicleData.model(),
                vehicleData.modelYear(),
                vehicleData.fuelType(),
                vehicleData.engine(),
                vehicleData.vehicleStatus(),
                SearchStatus.FOUND
        );

        searchHistoryRepository.save(searchHistory);
    }

    public void saveNoResults(Long userId, Long companyId, String vin, ProviderType provider) {
        log.info("Saving NO_RESULTS search history for vin={} provider={}", vin, provider);

        SearchHistory searchHistory = new SearchHistory(
                userId, companyId, vin, provider, SearchStatus.NO_RESULTS);

        searchHistoryRepository.save(searchHistory);
    }

    public void saveThirdPartyDown(Long userId, Long companyId, String vin, ProviderType provider) {
        log.warn("Saving THIRD_PARTY_DOWN search history for vin={} provider={}", vin, provider);

        SearchHistory searchHistory = new SearchHistory(
                userId, companyId, vin, provider, SearchStatus.THIRD_PARTY_DOWN);

        searchHistoryRepository.save(searchHistory);
    }
}