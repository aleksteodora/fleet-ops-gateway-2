package rs.tiacgroup.fleetopsgateway.statistics.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rs.tiacgroup.fleetopsgateway.statistics.dto.OutcomeStatistics;
import rs.tiacgroup.fleetopsgateway.statistics.dto.ProviderStatistics;
import rs.tiacgroup.fleetopsgateway.statistics.dto.VolumeStatistics;
import rs.tiacgroup.fleetopsgateway.statistics.service.StatisticsService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/providers")
    public ProviderStatistics getProviderStatistics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return statisticsService.getProviderStatistics(
                from != null ? from : LocalDateTime.now().minusYears(1),
                to != null ? to : LocalDateTime.now());
    }

    @GetMapping("/outcomes")
    public OutcomeStatistics getOutcomeStatistics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return statisticsService.getOutcomeStatistics(
                from != null ? from : LocalDateTime.now().minusYears(1),
                to != null ? to : LocalDateTime.now());
    }

    @GetMapping("/volume")
    public VolumeStatistics getVolumeStatistics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return statisticsService.getVolumeStatistics(
                from != null ? from : LocalDateTime.now().minusYears(1),
                to != null ? to : LocalDateTime.now());
    }
}