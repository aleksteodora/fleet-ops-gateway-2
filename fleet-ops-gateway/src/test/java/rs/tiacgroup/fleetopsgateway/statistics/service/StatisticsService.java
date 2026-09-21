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
import rs.tiacgroup.fleetopsgateway.searchhistory.repository.projection.CompanyOutcomeCount;
import rs.tiacgroup.fleetopsgateway.searchhistory.repository.projection.CompanyProviderCount;
import rs.tiacgroup.fleetopsgateway.searchhistory.repository.projection.OutcomeCount;
import rs.tiacgroup.fleetopsgateway.searchhistory.repository.projection.ProviderCount;
import rs.tiacgroup.fleetopsgateway.statistics.dto.OutcomeStatistics;
import rs.tiacgroup.fleetopsgateway.statistics.dto.ProviderStatistics;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {

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

        when(searchHistoryRepository.countByProvider()).thenReturn(List.of(freeTotal, premiumTotal));
        when(searchHistoryRepository.countByCompanyAndProvider()).thenReturn(
                List.of(company1Free, company1Premium, company2Free, company2Premium, company5Premium)
        );

        // when
        ProviderStatistics result = statisticsService.getProviderStatistics();

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
        when(searchHistoryRepository.countByProvider()).thenReturn(List.of());
        when(searchHistoryRepository.countByCompanyAndProvider()).thenReturn(List.of());

        // when
        ProviderStatistics result = statisticsService.getProviderStatistics();

        // then
        assertThat(result.totalByProvider())
                .containsEntry(ProviderType.FREE, 0L)
                .containsEntry(ProviderType.PREMIUM, 0L);

        assertThat(result.byCompanyAndProvider()).isEmpty();
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

    @Test
    void getOutcomeStatistics_shouldAggregateTotalsAndPerCompanyCorrectly() {
        // given
        OutcomeCount foundTotal = mockOutcomeCount(SearchStatus.FOUND, 53L);
        OutcomeCount noResultsTotal = mockOutcomeCount(SearchStatus.NO_RESULTS, 103L);
        OutcomeCount thirdPartyDownTotal = mockOutcomeCount(SearchStatus.THIRD_PARTY_DOWN, 16L);

        CompanyOutcomeCount company1Found = mockCompanyOutcomeCount(1L, SearchStatus.FOUND, 38L);
        CompanyOutcomeCount company5NoResults = mockCompanyOutcomeCount(5L, SearchStatus.NO_RESULTS, 34L);

        when(searchHistoryRepository.countByOutcome()).thenReturn(
                List.of(foundTotal, noResultsTotal, thirdPartyDownTotal)
        );
        when(searchHistoryRepository.countByCompanyAndOutcome()).thenReturn(
                List.of(company1Found, company5NoResults)
        );

        // when
        OutcomeStatistics result = statisticsService.getOutcomeStatistics();

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
        when(searchHistoryRepository.countByOutcome()).thenReturn(List.of());
        when(searchHistoryRepository.countByCompanyAndOutcome()).thenReturn(List.of());

        // when
        OutcomeStatistics result = statisticsService.getOutcomeStatistics();

        // then
        assertThat(result.totalByOutcome())
                .containsEntry(SearchStatus.FOUND, 0L)
                .containsEntry(SearchStatus.NO_RESULTS, 0L)
                .containsEntry(SearchStatus.THIRD_PARTY_DOWN, 0L);

        assertThat(result.byCompanyAndOutcome()).isEmpty();
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
}