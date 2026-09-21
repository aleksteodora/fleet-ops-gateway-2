package rs.tiacgroup.fleetopsgateway.statistics.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import rs.tiacgroup.fleetopsgateway.searchhistory.entity.ProviderType;
import rs.tiacgroup.fleetopsgateway.searchhistory.repository.SearchHistoryRepository;
import rs.tiacgroup.fleetopsgateway.searchhistory.repository.projection.CompanyProviderCount;
import rs.tiacgroup.fleetopsgateway.searchhistory.repository.projection.ProviderCount;
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

    private Map<ProviderType, Long> initializeProviderCounts() {
        Map<ProviderType, Long> counts = new HashMap<>();
        for (ProviderType provider : ProviderType.values()) {
            counts.put(provider, 0L);
        }
        return counts;
    }
}