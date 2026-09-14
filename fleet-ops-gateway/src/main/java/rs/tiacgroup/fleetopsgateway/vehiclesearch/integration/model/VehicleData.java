package rs.tiacgroup.fleetopsgateway.vehiclesearch.integration.model;

public record VehicleData(
        String make,
        String model,
        Integer modelYear,
        String fuelType,
        String engine,
        String vehicleStatus
) {
}