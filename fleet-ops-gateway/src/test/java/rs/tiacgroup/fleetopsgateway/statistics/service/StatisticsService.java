package rs.tiacgroup.fleetopsgateway.statistics.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
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
import rs.tiacgroup.fleetopsgateway.statistics.dto.UserVolumeStatistics;
import rs.tiacgroup.fleetopsgateway.statistics.dto.VolumeStatistics;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {

    private static final LocalDateTime FROM = LocalDateTime.of(2026, 1, 1, 0, 0);
    private static final LocalDateTime TO = LocalDateTime.of(2026, 12, 31, 23, 59);

    @Mock
    private SearchHistoryRepository searchHistoryRepository;

    @InjectMocks
    private StatisticsService statisticsService;

    @Test
    void getProviderStatistics_shouldAggregateTotalsAndPerCompanyCorrectly() {
        // given
        ProviderCount freeTotal = mockProviderCount(ProviderType.FREE, 30L);
        ProviderCount premiumTotal = mockProviderCount(ProviderType.PREMIUM, 142L);

        CompanyProviderCount company1Free = mockCompanyProviderCount(1L, ProviderType.FREE, 21L);
        CompanyProviderCount company1Premium = mockCompanyProviderCount(1L, ProviderType.PREMIUM, 97L);
        CompanyProviderCount company2Free = mockCompanyProviderCount(2L, ProviderType.FREE, 9L);
        CompanyProviderCount company2Premium = mockCompanyProviderCount(2L, ProviderType.PREMIUM, 8L);
        CompanyProviderCount company5Premium = mockCompanyProviderCount(5L, ProviderType.PREMIUM, 37L);

        when(searchHistoryRepository.countByProvider(FROM, TO)).thenReturn(List.of(freeTotal, premiumTotal));
        when(searchHistoryRepository.countByCompanyAndProvider(FROM, TO)).thenReturn(
                List.of(company1Free, company1Premium, company2Free, company2Premium, company5Premium)
        );

        // when
        ProviderStatistics result = statisticsService.getProviderStatistics(FROM, TO);

        // then
        assertThat(result.totalByProvider())
                .containsEntry(ProviderType.FREE, 30L)
                .containsEntry(ProviderType.PREMIUM, 142L);

        assertThat(result.byCompanyAndProvider().get(1L))
                .containsEntry(ProviderType.FREE, 21L)
                .containsEntry(ProviderType.PREMIUM, 97L);

        assertThat(result.byCompanyAndProvider().get(2L))
                .containsEntry(ProviderType.FREE, 9L)
                .containsEntry(ProviderType.PREMIUM, 8L);

        assertThat(result.byCompanyAndProvider().get(5L))
                .containsEntry(ProviderType.PREMIUM, 37L)
                .containsEntry(ProviderType.FREE, 0L);
    }

    @Test
    void getProviderStatistics_shouldReturnAllProvidersWithZeroWhenNoData() {
        // given
        when(searchHistoryRepository.countByProvider(FROM, TO)).thenReturn(List.of());
        when(searchHistoryRepository.countByCompanyAndProvider(FROM, TO)).thenReturn(List.of());

        // when
        ProviderStatistics result = statisticsService.getProviderStatistics(FROM, TO);

        // then
        assertThat(result.totalByProvider())
                .containsEntry(ProviderType.FREE, 0L)
                .containsEntry(ProviderType.PREMIUM, 0L);

        assertThat(result.byCompanyAndProvider()).isEmpty();
    }

    @Test
    void getOutcomeStatistics_shouldAggregateTotalsAndPerCompanyCorrectly() {
        // given
        OutcomeCount foundTotal = mockOutcomeCount(SearchStatus.FOUND, 53L);
        OutcomeCount noResultsTotal = mockOutcomeCount(SearchStatus.NO_RESULTS, 103L);
        OutcomeCount thirdPartyDownTotal = mockOutcomeCount(SearchStatus.THIRD_PARTY_DOWN, 16L);

        CompanyOutcomeCount company1Found = mockCompanyOutcomeCount(1L, SearchStatus.FOUND, 38L);
        CompanyOutcomeCount company5NoResults = mockCompanyOutcomeCount(5L, SearchStatus.NO_RESULTS, 34L);

        when(searchHistoryRepository.countByOutcome(FROM, TO)).thenReturn(
                List.of(foundTotal, noResultsTotal, thirdPartyDownTotal)
        );
        when(searchHistoryRepository.countByCompanyAndOutcome(FROM, TO)).thenReturn(
                List.of(company1Found, company5NoResults)
        );

        // when
        OutcomeStatistics result = statisticsService.getOutcomeStatistics(FROM, TO);

        // then
        assertThat(result.totalByOutcome())
                .containsEntry(SearchStatus.FOUND, 53L)
                .containsEntry(SearchStatus.NO_RESULTS, 103L)
                .containsEntry(SearchStatus.THIRD_PARTY_DOWN, 16L);

        assertThat(result.byCompanyAndOutcome().get(1L))
                .containsEntry(SearchStatus.FOUND, 38L);

        assertThat(result.byCompanyAndOutcome().get(5L))
                .containsEntry(SearchStatus.NO_RESULTS, 34L)
                .containsEntry(SearchStatus.FOUND, 0L)
                .containsEntry(SearchStatus.THIRD_PARTY_DOWN, 0L);
    }

    @Test
    void getOutcomeStatistics_shouldReturnAllOutcomesWithZeroWhenNoData() {
        // given
        when(searchHistoryRepository.countByOutcome(FROM, TO)).thenReturn(List.of());
        when(searchHistoryRepository.countByCompanyAndOutcome(FROM, TO)).thenReturn(List.of());

        // when
        OutcomeStatistics result = statisticsService.getOutcomeStatistics(FROM, TO);

        // then
        assertThat(result.totalByOutcome())
                .containsEntry(SearchStatus.FOUND, 0L)
                .containsEntry(SearchStatus.NO_RESULTS, 0L)
                .containsEntry(SearchStatus.THIRD_PARTY_DOWN, 0L);

        assertThat(result.byCompanyAndOutcome()).isEmpty();
    }

    @Test
    void getVolumeStatistics_shouldAggregateDailyAndWeeklyCorrectly() {
        // given
        LocalDate day1 = LocalDate.of(2026, 9, 15);
        LocalDate day2 = LocalDate.of(2026, 9, 18);
        LocalDate weekStart = LocalDate.of(2026, 9, 14);

        DailyCount day1Total = mockDailyCount(day1, 132L);
        DailyCount day2Total = mockDailyCount(day2, 40L);

        CompanyDailyCount company1Day1 = mockCompanyDailyCount(1L, day1, 78L);
        CompanyDailyCount company1Day2 = mockCompanyDailyCount(1L, day2, 40L);

        WeekBucketCount weekTotal = mockWeekBucketCount(weekStart, 172L);
        CompanyWeekBucketCount company1Week = mockCompanyWeekBucketCount(1L, weekStart, 118L);

        when(searchHistoryRepository.countByDay(FROM, TO)).thenReturn(List.of(day1Total, day2Total));
        when(searchHistoryRepository.countByCompanyAndDay(FROM, TO)).thenReturn(List.of(company1Day1, company1Day2));
        when(searchHistoryRepository.countByWeek(FROM, TO)).thenReturn(List.of(weekTotal));
        when(searchHistoryRepository.countByCompanyAndWeek(FROM, TO)).thenReturn(List.of(company1Week));

        // when
        VolumeStatistics result = statisticsService.getVolumeStatistics(FROM, TO);

        // then
        assertThat(result.dailyTotal())
                .containsEntry(day1, 132L)
                .containsEntry(day2, 40L);

        assertThat(result.dailyByCompany().get(1L))
                .containsEntry(day1, 78L)
                .containsEntry(day2, 40L);

        assertThat(result.weeklyTotal())
                .containsExactly(new VolumeStatistics.WeeklyCount(weekStart, 172L));

        assertThat(result.weeklyByCompany().get(1L))
                .containsExactly(new VolumeStatistics.WeeklyCount(weekStart, 118L));
    }

    @Test
    void getVolumeStatistics_shouldReturnEmptyWhenNoData() {
        // given
        when(searchHistoryRepository.countByDay(FROM, TO)).thenReturn(List.of());
        when(searchHistoryRepository.countByCompanyAndDay(FROM, TO)).thenReturn(List.of());
        when(searchHistoryRepository.countByWeek(FROM, TO)).thenReturn(List.of());
        when(searchHistoryRepository.countByCompanyAndWeek(FROM, TO)).thenReturn(List.of());

        // when
        VolumeStatistics result = statisticsService.getVolumeStatistics(FROM, TO);

        // then
        assertThat(result.dailyTotal()).isEmpty();
        assertThat(result.dailyByCompany()).isEmpty();
        assertThat(result.weeklyTotal()).isEmpty();
        assertThat(result.weeklyByCompany()).isEmpty();
    }

    private ProviderCount mockProviderCount(ProviderType provider, Long count) {
        ProviderCount mock = Mockito.mock(ProviderCount.class);
        when(mock.getProvider()).thenReturn(provider);
        when(mock.getCount()).thenReturn(count);
        return mock;
    }

    private CompanyProviderCount mockCompanyProviderCount(Long companyId, ProviderType provider, Long count) {
        CompanyProviderCount mock = Mockito.mock(CompanyProviderCount.class);
        when(mock.getCompanyId()).thenReturn(companyId);
        when(mock.getProvider()).thenReturn(provider);
        when(mock.getCount()).thenReturn(count);
        return mock;
    }

    private OutcomeCount mockOutcomeCount(SearchStatus status, Long count) {
        OutcomeCount mock = Mockito.mock(OutcomeCount.class);
        when(mock.getSearchStatus()).thenReturn(status);
        when(mock.getCount()).thenReturn(count);
        return mock;
    }

    private CompanyOutcomeCount mockCompanyOutcomeCount(Long companyId, SearchStatus status, Long count) {
        CompanyOutcomeCount mock = Mockito.mock(CompanyOutcomeCount.class);
        when(mock.getCompanyId()).thenReturn(companyId);
        when(mock.getSearchStatus()).thenReturn(status);
        when(mock.getCount()).thenReturn(count);
        return mock;
    }

    private DailyCount mockDailyCount(LocalDate date, Long count) {
        DailyCount mock = Mockito.mock(DailyCount.class);
        when(mock.getSearchDate()).thenReturn(date);
        when(mock.getCount()).thenReturn(count);
        return mock;
    }

    private CompanyDailyCount mockCompanyDailyCount(Long companyId, LocalDate date, Long count) {
        CompanyDailyCount mock = Mockito.mock(CompanyDailyCount.class);
        when(mock.getCompanyId()).thenReturn(companyId);
        when(mock.getSearchDate()).thenReturn(date);
        when(mock.getCount()).thenReturn(count);
        return mock;
    }

    private WeekBucketCount mockWeekBucketCount(LocalDate weekStart, Long count) {
        WeekBucketCount mock = Mockito.mock(WeekBucketCount.class);
        when(mock.getWeekStart()).thenReturn(weekStart);
        when(mock.getCount()).thenReturn(count);
        return mock;
    }

    private CompanyWeekBucketCount mockCompanyWeekBucketCount(Long companyId, LocalDate weekStart, Long count) {
        CompanyWeekBucketCount mock = Mockito.mock(CompanyWeekBucketCount.class);
        when(mock.getCompanyId()).thenReturn(companyId);
        when(mock.getWeekStart()).thenReturn(weekStart);
        when(mock.getCount()).thenReturn(count);
        return mock;
    }

    @Test
    void getMySearchStatistics_shouldAggregateDailyAndWeeklyForGivenUser() {
        // given
        Long userId = 1L;
        LocalDate day1 = LocalDate.of(2026, 9, 15);
        LocalDate day2 = LocalDate.of(2026, 9, 18);

        DailyCount day1Count = mockDailyCount(day1, 132L);
        DailyCount day2Count = mockDailyCount(day2, 1L);
        WeeklyCount week38Count = mockWeeklyCount(2026, 38, 133L);

        when(searchHistoryRepository.countByDayForUser(userId)).thenReturn(List.of(day1Count, day2Count));
        when(searchHistoryRepository.countByWeekForUser(userId)).thenReturn(List.of(week38Count));

        // when
        UserVolumeStatistics result = statisticsService.getMySearchStatistics(userId);

        // then
        assertThat(result.daily())
                .containsEntry(day1, 132L)
                .containsEntry(day2, 1L);

        assertThat(result.weekly())
                .containsExactly(new VolumeStatistics.WeeklyCount(2026, 38, 133L));
    }

    @Test
    void getMySearchStatistics_shouldReturnEmptyWhenUserHasNoSearches() {
        // given
        Long userId = 999L;
        when(searchHistoryRepository.countByDayForUser(userId)).thenReturn(List.of());
        when(searchHistoryRepository.countByWeekForUser(userId)).thenReturn(List.of());

        // when
        UserVolumeStatistics result = statisticsService.getMySearchStatistics(userId);

        // then
        assertThat(result.daily()).isEmpty();
        assertThat(result.weekly()).isEmpty();
    }
}