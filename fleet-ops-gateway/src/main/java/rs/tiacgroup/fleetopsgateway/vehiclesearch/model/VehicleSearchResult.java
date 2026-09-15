package rs.tiacgroup.fleetopsgateway.vehiclesearch.model;

import rs.tiacgroup.fleetopsgateway.searchhistory.entity.SearchStatus;
import rs.tiacgroup.fleetopsgateway.vehiclesearch.integration.model.VehicleData;

public record VehicleSearchResult(
        SearchStatus status,
        VehicleData vehicleData
) {
}