package rs.tiacgroup.fleetopsgateway.statistics.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record VolumeStatistics(
        Map<LocalDate, Long> dailyTotal,
        Map<Long, Map<LocalDate, Long>> dailyByCompany,
        List<WeeklyCount> weeklyTotal,
        Map<Long, List<WeeklyCount>> weeklyByCompany
) {
    public record WeeklyCount(LocalDate weekStart, Long count) {
    }
}