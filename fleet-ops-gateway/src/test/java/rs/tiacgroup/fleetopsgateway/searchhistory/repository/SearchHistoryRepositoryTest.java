package rs.tiacgroup.fleetopsgateway.searchhistory.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import rs.tiacgroup.fleetopsgateway.searchhistory.entity.ProviderType;
import rs.tiacgroup.fleetopsgateway.searchhistory.entity.SearchHistory;
import rs.tiacgroup.fleetopsgateway.searchhistory.entity.SearchStatus;

import java.util.List;
import java.util.Map;

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
        List<Object[]> result = searchHistoryRepository.countByProvider();

        // then
        Map<ProviderType, Long> counts = result.stream()
                .collect(java.util.stream.Collectors.toMap(
                        row -> (ProviderType) row[0],
                        row -> (Long) row[1]
                ));

        assertThat(counts.get(ProviderType.FREE)).isEqualTo(2L);
        assertThat(counts.get(ProviderType.PREMIUM)).isEqualTo(1L);
    }

    @Test
    void countByCompanyAndProvider_shouldGroupByCompanyAndProviderCorrectly() {
        // given
        searchHistoryRepository.save(new SearchHistory(1L, 1L, "VIN1", ProviderType.FREE, SearchStatus.FOUND));
        searchHistoryRepository.save(new SearchHistory(1L, 1L, "VIN2", ProviderType.PREMIUM, SearchStatus.FOUND));
        searchHistoryRepository.save(new SearchHistory(2L, 2L, "VIN3", ProviderType.FREE, SearchStatus.FOUND));

        // when
        List<Object[]> result = searchHistoryRepository.countByCompanyAndProvider();

        // then
        assertThat(result).hasSize(3);

        boolean hasCompany1Free = result.stream().anyMatch(row ->
                row[0].equals(1L) && row[1].equals(ProviderType.FREE) && row[2].equals(1L));
        boolean hasCompany1Premium = result.stream().anyMatch(row ->
                row[0].equals(1L) && row[1].equals(ProviderType.PREMIUM) && row[2].equals(1L));
        boolean hasCompany2Free = result.stream().anyMatch(row ->
                row[0].equals(2L) && row[1].equals(ProviderType.FREE) && row[2].equals(1L));

        assertThat(hasCompany1Free).isTrue();
        assertThat(hasCompany1Premium).isTrue();
        assertThat(hasCompany2Free).isTrue();
    }
}