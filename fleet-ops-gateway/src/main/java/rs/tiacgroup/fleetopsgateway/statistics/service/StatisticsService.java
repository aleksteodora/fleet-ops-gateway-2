package rs.tiacgroup.fleetopsgateway.statistics.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import rs.tiacgroup.fleetopsgateway.searchhistory.entity.ProviderType;
import rs.tiacgroup.fleetopsgateway.searchhistory.repository.SearchHistoryRepository;
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

        Map<ProviderType, Long> totalByProvider = new HashMap<>();
        for (Object[] row : searchHistoryRepository.countByProvider()) {
            ProviderType provider = (ProviderType) row[0];
            Long count = (Long) row[1];
            totalByProvider.put(provider, count);
        }

        Map<Long, Map<ProviderType, Long>> byCompanyAndProvider = new HashMap<>();
        for (Object[] row : searchHistoryRepository.countByCompanyAndProvider()) {
            Long companyId = (Long) row[0];
            ProviderType provider = (ProviderType) row[1];
            Long count = (Long) row[2];

            byCompanyAndProvider
                    .computeIfAbsent(companyId, id -> new HashMap<>())
                    .put(provider, count);
        }

        return new ProviderStatistics(totalByProvider, byCompanyAndProvider);
    }
}