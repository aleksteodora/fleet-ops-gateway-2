package rs.tiacgroup.fleetopsgateway.searchhistory.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import rs.tiacgroup.fleetopsgateway.searchhistory.entity.ProviderType;
import rs.tiacgroup.fleetopsgateway.searchhistory.entity.SearchHistory;
import rs.tiacgroup.fleetopsgateway.searchhistory.entity.SearchStatus;
import rs.tiacgroup.fleetopsgateway.searchhistory.repository.projection.CompanyDailyCount;
import rs.tiacgroup.fleetopsgateway.searchhistory.repository.projection.CompanyOutcomeCount;
import rs.tiacgroup.fleetopsgateway.searchhistory.repository.projection.CompanyProviderCount;
import rs.tiacgroup.fleetopsgateway.searchhistory.repository.projection.DailyCount;
import rs.tiacgroup.fleetopsgateway.searchhistory.repository.projection.OutcomeCount;
import rs.tiacgroup.fleetopsgateway.searchhistory.repository.projection.ProviderCount;
import rs.tiacgroup.fleetopsgateway.searchhistory.repository.projection.WeekBucketCount;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class SearchHistoryRepositoryTest {

    private static final LocalDateTime WIDE_FROM = LocalDateTime.of(2020, 1, 1, 0, 0);
    private static final LocalDateTime WIDE_TO = LocalDateTime.of(2030, 1, 1, 0, 0);

    @Autowired
    private SearchHistoryRepository searchHistoryRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void countByProvider_shouldGroupAndCountCorrectly() {
        // given
        searchHistoryRepository.save(new SearchHistory(1L, 1L, "VIN1", ProviderType.FREE, SearchStatus.FOUND));
        searchHistoryRepository.save(new SearchHistory(1L, 1L, "VIN2", ProviderType.FREE, SearchStatus.NO_RESULTS));
        searchHistoryRepository.save(new SearchHistory(1L, 1L, "VIN3", ProviderType.PREMIUM, SearchStatus.FOUND));

        // when
        List<ProviderCount> result = searchHistoryRepository.countByProvider(WIDE_FROM, WIDE_TO);

        // then
        ProviderCount freeCount = result.stream()
                .filter(row -> row.getProvider() == ProviderType.FREE)
                .findFirst()
                .orElseThrow();
        ProviderCount premiumCount = result.stream()
                .filter(row -> row.getProvider() == ProviderType.PREMIUM)
                .findFirst()
                .orElseThrow();

        assertThat(freeCount.getCount()).isEqualTo(2L);
        assertThat(premiumCount.getCount()).isEqualTo(1L);
    }

    @Test
    void countByProvider_shouldExcludeRecordsOutsideRange() {
        // given
        LocalDateTime insideRange = LocalDateTime.of(2026, 6, 15, 10, 0);
        LocalDateTime outsideRange = LocalDateTime.of(2020, 1, 1, 10, 0);

        saveWithSearchedAt(1L, 1L, "VIN1", insideRange);
        saveWithSearchedAt(1L, 1L, "VIN2", outsideRange);

        // when
        List<ProviderCount> result = searchHistoryRepository.countByProvider(
                LocalDateTime.of(2026, 1, 1, 0, 0), LocalDateTime.of(2027, 1, 1, 0, 0));

        // then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getCount()).isEqualTo(1L);
    }

    @Test
    void countByCompanyAndProvider_shouldGroupByCompanyAndProviderCorrectly() {
        // given
        searchHistoryRepository.save(new SearchHistory(1L, 1L, "VIN1", ProviderType.FREE, SearchStatus.FOUND));
        searchHistoryRepository.save(new SearchHistory(1L, 1L, "VIN2", ProviderType.PREMIUM, SearchStatus.FOUND));
        searchHistoryRepository.save(new SearchHistory(2L, 2L, "VIN3", ProviderType.FREE, SearchStatus.FOUND));

        // when
        List<CompanyProviderCount> result = searchHistoryRepository.countByCompanyAndProvider(WIDE_FROM, WIDE_TO);

        // then
        assertThat(result).hasSize(3);

        boolean hasCompany1Free = result.stream().anyMatch(row ->
                row.getCompanyId().equals(1L) && row.getProvider() == ProviderType.FREE && row.getCount().equals(1L));
        boolean hasCompany1Premium = result.stream().anyMatch(row ->
                row.getCompanyId().equals(1L) && row.getProvider() == ProviderType.PREMIUM && row.getCount().equals(1L));
        boolean hasCompany2Free = result.stream().anyMatch(row ->
                row.getCompanyId().equals(2L) && row.getProvider() == ProviderType.FREE && row.getCount().equals(1L));

        assertThat(hasCompany1Free).isTrue();
        assertThat(hasCompany1Premium).isTrue();
        assertThat(hasCompany2Free).isTrue();
    }

    @Test
    void countByOutcome_shouldGroupAndCountCorrectly() {
        // given
        searchHistoryRepository.save(new SearchHistory(1L, 1L, "VIN1", ProviderType.FREE, SearchStatus.FOUND));
        searchHistoryRepository.save(new SearchHistory(1L, 1L, "VIN2", ProviderType.FREE, SearchStatus.NO_RESULTS));
        searchHistoryRepository.save(new SearchHistory(1L, 1L, "VIN3", ProviderType.PREMIUM, SearchStatus.NO_RESULTS));

        // when
        List<OutcomeCount> result = searchHistoryRepository.countByOutcome(WIDE_FROM, WIDE_TO);

        // then
        OutcomeCount foundCount = result.stream()
                .filter(row -> row.getSearchStatus() == SearchStatus.FOUND)
                .findFirst()
                .orElseThrow();
        OutcomeCount noResultsCount = result.stream()
                .filter(row -> row.getSearchStatus() == SearchStatus.NO_RESULTS)
                .findFirst()
                .orElseThrow();

        assertThat(foundCount.getCount()).isEqualTo(1L);
        assertThat(noResultsCount.getCount()).isEqualTo(2L);
    }

    @Test
    void countByCompanyAndOutcome_shouldGroupByCompanyAndOutcomeCorrectly() {
        // given
        searchHistoryRepository.save(new SearchHistory(1L, 1L, "VIN1", ProviderType.FREE, SearchStatus.FOUND));
        searchHistoryRepository.save(new SearchHistory(1L, 1L, "VIN2", ProviderType.PREMIUM, SearchStatus.THIRD_PARTY_DOWN));
        searchHistoryRepository.save(new SearchHistory(2L, 2L, "VIN3", ProviderType.FREE, SearchStatus.NO_RESULTS));

        // when
        List<CompanyOutcomeCount> result = searchHistoryRepository.countByCompanyAndOutcome(WIDE_FROM, WIDE_TO);

        // then
        assertThat(result).hasSize(3);

        boolean hasCompany1Found = result.stream().anyMatch(row ->
                row.getCompanyId().equals(1L) && row.getSearchStatus() == SearchStatus.FOUND && row.getCount().equals(1L));
        boolean hasCompany1ThirdPartyDown = result.stream().anyMatch(row ->
                row.getCompanyId().equals(1L) && row.getSearchStatus() == SearchStatus.THIRD_PARTY_DOWN && row.getCount().equals(1L));
        boolean hasCompany2NoResults = result.stream().anyMatch(row ->
                row.getCompanyId().equals(2L) && row.getSearchStatus() == SearchStatus.NO_RESULTS && row.getCount().equals(1L));

        assertThat(hasCompany1Found).isTrue();
        assertThat(hasCompany1ThirdPartyDown).isTrue();
        assertThat(hasCompany2NoResults).isTrue();
    }

    @Test
    void countByDay_shouldGroupAndCountCorrectly() {
        // given
        LocalDateTime day1 = LocalDateTime.of(2026, 9, 15, 10, 0);
        LocalDateTime day1Later = LocalDateTime.of(2026, 9, 15, 16, 0);
        LocalDateTime day2 = LocalDateTime.of(2026, 9, 18, 9, 0);

        saveWithSearchedAt(1L, 1L, "VIN1", day1);
        saveWithSearchedAt(1L, 1L, "VIN2", day1Later);
        saveWithSearchedAt(1L, 1L, "VIN3", day2);

        // when
        List<DailyCount> result = searchHistoryRepository.countByDay(WIDE_FROM, WIDE_TO);

        // then
        DailyCount day1Count = result.stream()
                .filter(row -> row.getSearchDate().equals(LocalDate.of(2026, 9, 15)))
                .findFirst()
                .orElseThrow();
        DailyCount day2Count = result.stream()
                .filter(row -> row.getSearchDate().equals(LocalDate.of(2026, 9, 18)))
                .findFirst()
                .orElseThrow();

        assertThat(day1Count.getCount()).isEqualTo(2L);
        assertThat(day2Count.getCount()).isEqualTo(1L);
    }

    @Test
    void countByCompanyAndDay_shouldGroupByCompanyAndDayCorrectly() {
        // given
        LocalDateTime day1 = LocalDateTime.of(2026, 9, 15, 10, 0);

        saveWithSearchedAt(1L, 1L, "VIN1", day1);
        saveWithSearchedAt(2L, 2L, "VIN2", day1);

        // when
        List<CompanyDailyCount> result = searchHistoryRepository.countByCompanyAndDay(WIDE_FROM, WIDE_TO);

        // then
        assertThat(result).hasSize(2);

        boolean hasCompany1 = result.stream().anyMatch(row ->
                row.getCompanyId().equals(1L) && row.getSearchDate().equals(LocalDate.of(2026, 9, 15)) && row.getCount().equals(1L));
        boolean hasCompany2 = result.stream().anyMatch(row ->
                row.getCompanyId().equals(2L) && row.getSearchDate().equals(LocalDate.of(2026, 9, 15)) && row.getCount().equals(1L));

        assertThat(hasCompany1).isTrue();
        assertThat(hasCompany2).isTrue();
    }

    @Test
    void countByWeek_shouldGroupAndCountCorrectly() {
        // given
        LocalDateTime dateInWeek = LocalDateTime.of(2026, 9, 15, 10, 0);

        saveWithSearchedAt(1L, 1L, "VIN1", dateInWeek);
        saveWithSearchedAt(1L, 1L, "VIN2", dateInWeek.plusDays(1));

        // when
        List<WeekBucketCount> result = searchHistoryRepository.countByWeek(WIDE_FROM, WIDE_TO);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getCount()).isEqualTo(2L);
    }

    @Test
    void countByWeek_shouldBucketNewYearDateIntoCorrectWeek() {
        // given
        LocalDateTime newYearsDay = LocalDateTime.of(2027, 1, 1, 10, 0);

        saveWithSearchedAt(1L, 1L, "VIN1", newYearsDay);

        // when
        List<WeekBucketCount> result = searchHistoryRepository.countByWeek(WIDE_FROM, WIDE_TO);

        // then
        assertThat(result).hasSize(1);
        LocalDate weekStart = result.getFirst().getWeekStart();
        assertThat(weekStart.getYear()).isEqualTo(2026);
        assertThat(result.getFirst().getCount()).isEqualTo(1L);
    }

    private void saveWithSearchedAt(Long userId, Long companyId, String vin, LocalDateTime searchedAt) {
        SearchHistory history = new SearchHistory(userId, companyId, vin, ProviderType.FREE, SearchStatus.FOUND);
        searchHistoryRepository.save(history);
        searchHistoryRepository.flush();
        jdbcTemplate.update("UPDATE search_histories SET searched_at = ? WHERE id = ?", searchedAt, history.getId());
    }

    @Test
    void countByDayForUser_shouldReturnOnlyThatUsersSearches() {
        // given
        LocalDateTime day1 = LocalDateTime.of(2026, 9, 15, 10, 0);

        saveWithUserAndSearchedAt(1L, "VIN1", day1);
        saveWithUserAndSearchedAt(1L, "VIN2", day1);
        saveWithUserAndSearchedAt(2L, "VIN3", day1);

        // when
        List<DailyCount> result = searchHistoryRepository.countByDayForUser(1L);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getCount()).isEqualTo(2L);
    }

    @Test
    void countByWeekForUser_shouldReturnOnlyThatUsersSearches() {
        // given
        LocalDateTime day1 = LocalDateTime.of(2026, 9, 15, 10, 0);

        saveWithUserAndSearchedAt(1L, "VIN1", day1);
        saveWithUserAndSearchedAt(2L, "VIN2", day1);
        saveWithUserAndSearchedAt(2L, "VIN3", day1);

        // when
        List<WeeklyCount> result = searchHistoryRepository.countByWeekForUser(2L);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getCount()).isEqualTo(2L);
    }

    private void saveWithUserAndSearchedAt(Long userId, String vin, LocalDateTime searchedAt) {
        SearchHistory history = new SearchHistory(userId, 1L, vin, ProviderType.FREE, SearchStatus.FOUND);
        searchHistoryRepository.save(history);
        searchHistoryRepository.flush();
        jdbcTemplate.update("UPDATE search_histories SET searched_at = ? WHERE id = ?", searchedAt, history.getId());
    }
}