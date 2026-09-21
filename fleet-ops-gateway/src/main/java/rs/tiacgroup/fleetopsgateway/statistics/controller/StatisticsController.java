package rs.tiacgroup.fleetopsgateway.statistics.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.tiacgroup.fleetopsgateway.statistics.dto.OutcomeStatistics;
import rs.tiacgroup.fleetopsgateway.statistics.dto.ProviderStatistics;
import rs.tiacgroup.fleetopsgateway.statistics.dto.VolumeStatistics;
import rs.tiacgroup.fleetopsgateway.statistics.service.StatisticsService;

@RestController
@RequestMapping("/api/v1/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/providers")
    public ProviderStatistics getProviderStatistics() {
        return statisticsService.getProviderStatistics();
    }

    @GetMapping("/outcomes")
    public OutcomeStatistics getOutcomeStatistics() {
        return statisticsService.getOutcomeStatistics();
    }

    @GetMapping("/volume")
    public VolumeStatistics getVolumeStatistics() {
        return statisticsService.getVolumeStatistics();
    }
}