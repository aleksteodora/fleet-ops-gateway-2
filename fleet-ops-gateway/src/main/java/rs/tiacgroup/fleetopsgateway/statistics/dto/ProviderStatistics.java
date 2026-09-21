package rs.tiacgroup.fleetopsgateway.statistics.dto;

import rs.tiacgroup.fleetopsgateway.searchhistory.entity.ProviderType;

import java.util.Map;

public record ProviderStatistics(
        Map<ProviderType, Long> totalByProvider,
        Map<Long, Map<ProviderType, Long>> byCompanyAndProvider
) {
}