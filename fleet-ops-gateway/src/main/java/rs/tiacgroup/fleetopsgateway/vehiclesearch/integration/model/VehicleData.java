package rs.tiacgroup.fleetopsgateway.vehiclesearch.integration.model;

import rs.tiacgroup.fleetopsgateway.searchhistory.entity.FuelType;
import rs.tiacgroup.fleetopsgateway.searchhistory.entity.VehicleStatus;

public record VehicleData(
        String make,
        String model,
        Integer modelYear,
        FuelType fuelType,
        String engine,
        VehicleStatus vehicleStatus
) {
}