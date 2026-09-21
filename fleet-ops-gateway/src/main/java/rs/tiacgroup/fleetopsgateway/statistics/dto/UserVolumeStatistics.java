package rs.tiacgroup.fleetopsgateway.statistics.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record UserVolumeStatistics(
        Map<LocalDate, Long> daily,
        List<VolumeStatistics.WeeklyCount> weekly
) {
}