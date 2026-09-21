package rs.tiacgroup.fleetopsgateway.statistics.dto;

import rs.tiacgroup.fleetopsgateway.searchhistory.entity.SearchStatus;

import java.util.Map;

public record OutcomeStatistics(
        Map<SearchStatus, Long> totalByOutcome,
        Map<Long, Map<SearchStatus, Long>> byCompanyAndOutcome
) {
}