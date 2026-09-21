package rs.tiacgroup.fleetopsgateway.statistics.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import rs.tiacgroup.fleetopsgateway.searchhistory.entity.ProviderType;
import rs.tiacgroup.fleetopsgateway.searchhistory.entity.SearchStatus;
import rs.tiacgroup.fleetopsgateway.searchhistory.repository.SearchHistoryRepository;
import rs.tiacgroup.fleetopsgateway.searchhistory.repository.projection.CompanyOutcomeCount;
import rs.tiacgroup.fleetopsgateway.searchhistory.repository.projection.CompanyProviderCount;
import rs.tiacgroup.fleetopsgateway.searchhistory.repository.projection.OutcomeCount;
import rs.tiacgroup.fleetopsgateway.searchhistory.repository.projection.ProviderCount;
import rs.tiacgroup.fleetopsgateway.statistics.dto.OutcomeStatistics;
import rs.tiacgroup.fleetopsgateway.statistics.dto.ProviderStatistics;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class StatisticsService {

    private final SearchHistoryRepository searchHistoryRepository;

    public StatisticsService(SearchHistoryRepository searchHistoryRepository) {
        this.searchHistoryRepository = searchHistoryRepository;
    }

    public ProviderStatistics getProviderStatistics() {
        log.debug("Calculating provider usage statistics");

        Map<ProviderType, Long> totalByProvider = initializeProviderCounts();
        for (ProviderCount row : searchHistoryRepository.countByProvider()) {
            totalByProvider.put(row.getProvider(), row.getCount());
        }

        Map<Long, Map<ProviderType, Long>> byCompanyAndProvider = new HashMap<>();
        for (CompanyProviderCount row : searchHistoryRepository.countByCompanyAndProvider()) {
            byCompanyAndProvider
                    .computeIfAbsent(row.getCompanyId(), id -> initializeProviderCounts())
                    .put(row.getProvider(), row.getCount());
        }

        return new ProviderStatistics(totalByProvider, byCompanyAndProvider);
    }

    public OutcomeStatistics getOutcomeStatistics() {
        log.debug("Calculating search outcome statistics");

        Map<SearchStatus, Long> totalByOutcome = initializeOutcomeCounts();
        for (OutcomeCount row : searchHistoryRepository.countByOutcome()) {
            totalByOutcome.put(row.getSearchStatus(), row.getCount());
        }

        Map<Long, Map<SearchStatus, Long>> byCompanyAndOutcome = new HashMap<>();
        for (CompanyOutcomeCount row : searchHistoryRepository.countByCompanyAndOutcome()) {
            byCompanyAndOutcome
                    .computeIfAbsent(row.getCompanyId(), id -> initializeOutcomeCounts())
                    .put(row.getSearchStatus(), row.getCount());
        }

        return new OutcomeStatistics(totalByOutcome, byCompanyAndOutcome);
    }

    private Map<ProviderType, Long> initializeProviderCounts() {
        Map<ProviderType, Long> counts = new HashMap<>();
        for (ProviderType provider : ProviderType.values()) {
            counts.put(provider, 0L);
        }
        return counts;
    }

    private Map<SearchStatus, Long> initializeOutcomeCounts() {
        Map<SearchStatus, Long> counts = new HashMap<>();
        for (SearchStatus status : SearchStatus.values()) {
            counts.put(status, 0L);
        }
        return counts;
    }
}