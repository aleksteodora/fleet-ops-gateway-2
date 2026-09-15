package rs.tiacgroup.fleetopsgateway.vehiclesearch.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import rs.tiacgroup.fleetopsgateway.searchhistory.entity.ProviderType;
import rs.tiacgroup.fleetopsgateway.searchhistory.entity.SearchStatus;
import rs.tiacgroup.fleetopsgateway.searchhistory.service.SearchHistoryService;
import rs.tiacgroup.fleetopsgateway.vehiclesearch.integration.FreeProviderClient;
import rs.tiacgroup.fleetopsgateway.vehiclesearch.integration.PremiumProviderClient;
import rs.tiacgroup.fleetopsgateway.vehiclesearch.integration.VehicleProviderResult;
import rs.tiacgroup.fleetopsgateway.vehiclesearch.model.VehicleSearchResult;

@Service
@Slf4j
public class VehicleSearchService {

    private final FreeProviderClient freeProviderClient;
    private final PremiumProviderClient premiumProviderClient;
    private final SearchHistoryService searchHistoryService;

    public VehicleSearchService(FreeProviderClient freeProviderClient,
                                PremiumProviderClient premiumProviderClient,
                                SearchHistoryService searchHistoryService) {
        this.freeProviderClient = freeProviderClient;
        this.premiumProviderClient = premiumProviderClient;
        this.searchHistoryService = searchHistoryService;
    }

    public VehicleSearchResult search(String vin, Long userId, Long companyId) {
        log.info("Starting vehicle search for vin={} userId={} companyId={}", vin, userId, companyId);

        VehicleProviderResult freeResult = freeProviderClient.search(vin);

        if (freeResult.outcome() == VehicleProviderResult.Outcome.FOUND) {
            searchHistoryService.saveFound(userId, companyId, vin, ProviderType.FREE, freeResult.vehicleData());
            return new VehicleSearchResult(SearchStatus.FOUND, freeResult.vehicleData());
        }

        log.info("FREE provider did not return a result (outcome={}), falling back to PREMIUM for vin={}",
                freeResult.outcome(), vin);

        VehicleProviderResult premiumResult = premiumProviderClient.search(vin);

        return switch (premiumResult.outcome()) {
            case FOUND -> {
                searchHistoryService.saveFound(userId, companyId, vin, ProviderType.PREMIUM, premiumResult.vehicleData());
                yield new VehicleSearchResult(SearchStatus.FOUND, premiumResult.vehicleData());
            }
            case NOT_FOUND -> {
                searchHistoryService.saveNoResults(userId, companyId, vin, ProviderType.PREMIUM);
                yield new VehicleSearchResult(SearchStatus.NO_RESULTS, null);
            }
            case UNAVAILABLE -> {
                searchHistoryService.saveThirdPartyDown(userId, companyId, vin, ProviderType.PREMIUM);
                yield new VehicleSearchResult(SearchStatus.THIRD_PARTY_DOWN, null);
            }
        };
    }
}