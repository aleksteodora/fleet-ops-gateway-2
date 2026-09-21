package rs.tiacgroup.fleetopsgateway.statistics.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rs.tiacgroup.fleetopsgateway.searchhistory.entity.ProviderType;
import rs.tiacgroup.fleetopsgateway.searchhistory.repository.SearchHistoryRepository;
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
        when(searchHistoryRepository.countByProvider()).thenReturn(List.of(
                new Object[]{ProviderType.FREE, 30L},
                new Object[]{ProviderType.PREMIUM, 142L}
        ));

        when(searchHistoryRepository.countByCompanyAndProvider()).thenReturn(List.of(
                new Object[]{1L, ProviderType.FREE, 21L},
                new Object[]{1L, ProviderType.PREMIUM, 97L},
                new Object[]{2L, ProviderType.FREE, 9L},
                new Object[]{2L, ProviderType.PREMIUM, 8L},
                new Object[]{5L, ProviderType.PREMIUM, 37L}
        ));

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
                .doesNotContainKey(ProviderType.FREE);
    }

    @Test
    void getProviderStatistics_shouldReturnEmptyMapsWhenNoData() {
        // given
        when(searchHistoryRepository.countByProvider()).thenReturn(List.of());
        when(searchHistoryRepository.countByCompanyAndProvider()).thenReturn(List.of());

        // when
        ProviderStatistics result = statisticsService.getProviderStatistics();

        // then
        assertThat(result.totalByProvider()).isEmpty();
        assertThat(result.byCompanyAndProvider()).isEmpty();
    }
}