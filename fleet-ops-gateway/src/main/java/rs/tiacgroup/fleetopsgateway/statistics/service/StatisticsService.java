package rs.tiacgroup.fleetopsgateway.statistics.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import rs.tiacgroup.fleetopsgateway.searchhistory.entity.ProviderType;
import rs.tiacgroup.fleetopsgateway.searchhistory.entity.SearchStatus;
import rs.tiacgroup.fleetopsgateway.searchhistory.repository.SearchHistoryRepository;
import rs.tiacgroup.fleetopsgateway.searchhistory.repository.projection.*;
import rs.tiacgroup.fleetopsgateway.statistics.dto.OutcomeStatistics;
import rs.tiacgroup.fleetopsgateway.statistics.dto.ProviderStatistics;
import rs.tiacgroup.fleetopsgateway.statistics.dto.VolumeStatistics;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    public VolumeStatistics getVolumeStatistics() {
        log.debug("Calculating search volume statistics");

        Map<LocalDate, Long> dailyTotal = new HashMap<>();
        for (DailyCount row : searchHistoryRepository.countByDay()) {
            dailyTotal.put(row.getSearchDate(), row.getCount());
        }

        Map<Long, Map<LocalDate, Long>> dailyByCompany = new HashMap<>();
        for (CompanyDailyCount row : searchHistoryRepository.countByCompanyAndDay()) {
            dailyByCompany
                    .computeIfAbsent(row.getCompanyId(), id -> new HashMap<>())
                    .put(row.getSearchDate(), row.getCount());
        }

        List<VolumeStatistics.WeeklyCount> weeklyTotal = new ArrayList<>();
        for (WeeklyCount row : searchHistoryRepository.countByWeek()) {
            weeklyTotal.add(new VolumeStatistics.WeeklyCount(row.getYear(), row.getWeek(), row.getCount()));
        }

        Map<Long, List<VolumeStatistics.WeeklyCount>> weeklyByCompany = new HashMap<>();
        for (CompanyWeeklyCount row : searchHistoryRepository.countByCompanyAndWeek()) {
            weeklyByCompany
                    .computeIfAbsent(row.getCompanyId(), id -> new ArrayList<>())
                    .add(new VolumeStatistics.WeeklyCount(row.getYear(), row.getWeek(), row.getCount()));
        }

        return new VolumeStatistics(dailyTotal, dailyByCompany, weeklyTotal, weeklyByCompany);
    }
}