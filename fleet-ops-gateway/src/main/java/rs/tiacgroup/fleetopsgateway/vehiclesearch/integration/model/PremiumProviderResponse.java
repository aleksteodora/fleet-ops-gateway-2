package rs.tiacgroup.fleetopsgateway.vehiclesearch.integration.model;

public record PremiumProviderResponse(
        String make,
        String model,
        Integer modelYear,
        String fuelType,
        String engine,
        String vehicleStatus
) {
}