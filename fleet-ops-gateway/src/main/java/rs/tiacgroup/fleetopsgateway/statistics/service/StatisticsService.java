package rs.tiacgroup.fleetopsgateway.statistics.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.tiacgroup.fleetopsgateway.searchhistory.entity.ProviderType;
import rs.tiacgroup.fleetopsgateway.searchhistory.entity.SearchStatus;
import rs.tiacgroup.fleetopsgateway.searchhistory.repository.SearchHistoryRepository;
import rs.tiacgroup.fleetopsgateway.searchhistory.repository.projection.CompanyDailyCount;
import rs.tiacgroup.fleetopsgateway.searchhistory.repository.projection.CompanyOutcomeCount;
import rs.tiacgroup.fleetopsgateway.searchhistory.repository.projection.CompanyProviderCount;
import rs.tiacgroup.fleetopsgateway.searchhistory.repository.projection.CompanyWeekBucketCount;
import rs.tiacgroup.fleetopsgateway.searchhistory.repository.projection.DailyCount;
import rs.tiacgroup.fleetopsgateway.searchhistory.repository.projection.OutcomeCount;
import rs.tiacgroup.fleetopsgateway.searchhistory.repository.projection.ProviderCount;
import rs.tiacgroup.fleetopsgateway.searchhistory.repository.projection.WeekBucketCount;
import rs.tiacgroup.fleetopsgateway.statistics.dto.OutcomeStatistics;
import rs.tiacgroup.fleetopsgateway.statistics.dto.ProviderStatistics;
import rs.tiacgroup.fleetopsgateway.statistics.dto.VolumeStatistics;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
@Slf4j
public class StatisticsService {

    private final SearchHistoryRepository searchHistoryRepository;

    public StatisticsService(SearchHistoryRepository searchHistoryRepository) {
        this.searchHistoryRepository = searchHistoryRepository;
    }

    @Transactional(readOnly = true)
    public ProviderStatistics getProviderStatistics(LocalDateTime from, LocalDateTime to) {
        log.debug("Calculating provider usage statistics from={} to={}", from, to);

        Map<ProviderType, Long> totalByProvider = initializeProviderCounts();
        for (ProviderCount row : searchHistoryRepository.countByProvider(from, to)) {
            totalByProvider.put(row.getProvider(), row.getCount());
        }

        Map<Long, Map<ProviderType, Long>> byCompanyAndProvider = new HashMap<>();
        for (CompanyProviderCount row : searchHistoryRepository.countByCompanyAndProvider(from, to)) {
            byCompanyAndProvider
                    .computeIfAbsent(row.getCompanyId(), id -> initializeProviderCounts())
                    .put(row.getProvider(), row.getCount());
        }

        return new ProviderStatistics(totalByProvider, byCompanyAndProvider);
    }

    @Transactional(readOnly = true)
    public OutcomeStatistics getOutcomeStatistics(LocalDateTime from, LocalDateTime to) {
        log.debug("Calculating search outcome statistics from={} to={}", from, to);

        Map<SearchStatus, Long> totalByOutcome = initializeOutcomeCounts();
        for (OutcomeCount row : searchHistoryRepository.countByOutcome(from, to)) {
            totalByOutcome.put(row.getSearchStatus(), row.getCount());
        }

        Map<Long, Map<SearchStatus, Long>> byCompanyAndOutcome = new HashMap<>();
        for (CompanyOutcomeCount row : searchHistoryRepository.countByCompanyAndOutcome(from, to)) {
            byCompanyAndOutcome
                    .computeIfAbsent(row.getCompanyId(), id -> initializeOutcomeCounts())
                    .put(row.getSearchStatus(), row.getCount());
        }

        return new OutcomeStatistics(totalByOutcome, byCompanyAndOutcome);
    }

    @Transactional(readOnly = true)
    public VolumeStatistics getVolumeStatistics(LocalDateTime from, LocalDateTime to) {
        log.debug("Calculating search volume statistics from={} to={}", from, to);

        Map<LocalDate, Long> dailyTotal = new TreeMap<>();
        for (DailyCount row : searchHistoryRepository.countByDay(from, to)) {
            dailyTotal.put(row.getSearchDate(), row.getCount());
        }

        Map<Long, Map<LocalDate, Long>> dailyByCompany = new HashMap<>();
        for (CompanyDailyCount row : searchHistoryRepository.countByCompanyAndDay(from, to)) {
            dailyByCompany
                    .computeIfAbsent(row.getCompanyId(), id -> new TreeMap<>())
                    .put(row.getSearchDate(), row.getCount());
        }

        List<VolumeStatistics.WeeklyCount> weeklyTotal = new ArrayList<>();
        for (WeekBucketCount row : searchHistoryRepository.countByWeek(from, to)) {
            weeklyTotal.add(new VolumeStatistics.WeeklyCount(row.getWeekStart(), row.getCount()));
        }

        Map<Long, List<VolumeStatistics.WeeklyCount>> weeklyByCompany = new HashMap<>();
        for (CompanyWeekBucketCount row : searchHistoryRepository.countByCompanyAndWeek(from, to)) {
            weeklyByCompany
                    .computeIfAbsent(row.getCompanyId(), id -> new ArrayList<>())
                    .add(new VolumeStatistics.WeeklyCount(row.getWeekStart(), row.getCount()));
        }

        return new VolumeStatistics(dailyTotal, dailyByCompany, weeklyTotal, weeklyByCompany);
    }

    private Map<ProviderType, Long> initializeProviderCounts() {
        Map<ProviderType, Long> counts = new EnumMap<>(ProviderType.class);
        for (ProviderType provider : ProviderType.values()) {
            counts.put(provider, 0L);
        }
        return counts;
    }

    private Map<SearchStatus, Long> initializeOutcomeCounts() {
        Map<SearchStatus, Long> counts = new EnumMap<>(SearchStatus.class);
        for (SearchStatus status : SearchStatus.values()) {
            counts.put(status, 0L);
        }
        return counts;
    }
}