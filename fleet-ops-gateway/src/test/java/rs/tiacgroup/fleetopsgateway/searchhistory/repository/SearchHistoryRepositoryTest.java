package rs.tiacgroup.fleetopsgateway.searchhistory.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import rs.tiacgroup.fleetopsgateway.searchhistory.entity.ProviderType;
import rs.tiacgroup.fleetopsgateway.searchhistory.entity.SearchHistory;
import rs.tiacgroup.fleetopsgateway.searchhistory.entity.SearchStatus;
import rs.tiacgroup.fleetopsgateway.searchhistory.repository.projection.CompanyOutcomeCount;
import rs.tiacgroup.fleetopsgateway.searchhistory.repository.projection.CompanyProviderCount;
import rs.tiacgroup.fleetopsgateway.searchhistory.repository.projection.OutcomeCount;
import rs.tiacgroup.fleetopsgateway.searchhistory.repository.projection.ProviderCount;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class SearchHistoryRepositoryTest {

    @Autowired
    private SearchHistoryRepository searchHistoryRepository;

    @Test
    void countByProvider_shouldGroupAndCountCorrectly() {
        // given
        searchHistoryRepository.save(new SearchHistory(1L, 1L, "VIN1", ProviderType.FREE, SearchStatus.FOUND));
        searchHistoryRepository.save(new SearchHistory(1L, 1L, "VIN2", ProviderType.FREE, SearchStatus.NO_RESULTS));
        searchHistoryRepository.save(new SearchHistory(1L, 1L, "VIN3", ProviderType.PREMIUM, SearchStatus.FOUND));

        // when
        List<ProviderCount> result = searchHistoryRepository.countByProvider();

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
    void countByCompanyAndProvider_shouldGroupByCompanyAndProviderCorrectly() {
        // given
        searchHistoryRepository.save(new SearchHistory(1L, 1L, "VIN1", ProviderType.FREE, SearchStatus.FOUND));
        searchHistoryRepository.save(new SearchHistory(1L, 1L, "VIN2", ProviderType.PREMIUM, SearchStatus.FOUND));
        searchHistoryRepository.save(new SearchHistory(2L, 2L, "VIN3", ProviderType.FREE, SearchStatus.FOUND));

        // when
        List<CompanyProviderCount> result = searchHistoryRepository.countByCompanyAndProvider();

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
        List<OutcomeCount> result = searchHistoryRepository.countByOutcome();

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
        List<CompanyOutcomeCount> result = searchHistoryRepository.countByCompanyAndOutcome();

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
}