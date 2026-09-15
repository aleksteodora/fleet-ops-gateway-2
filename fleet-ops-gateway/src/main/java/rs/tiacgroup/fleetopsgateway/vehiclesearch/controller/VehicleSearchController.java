package rs.tiacgroup.fleetopsgateway.vehiclesearch.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rs.tiacgroup.fleetopsgateway.vehiclesearch.model.VehicleSearchResult;
import rs.tiacgroup.fleetopsgateway.vehiclesearch.service.VehicleSearchService;

@RestController
@RequestMapping("/api/v1/vehicle-search")
public class VehicleSearchController {

    private final VehicleSearchService vehicleSearchService;

    public VehicleSearchController(VehicleSearchService vehicleSearchService) {
        this.vehicleSearchService = vehicleSearchService;
    }

    @GetMapping
    public VehicleSearchResult search(@RequestParam String vin,
                                      @RequestHeader("X-User-Id") Long userId,
                                      @RequestHeader("X-Company-Id") Long companyId) {
        return vehicleSearchService.search(vin, userId, companyId);
    }
}