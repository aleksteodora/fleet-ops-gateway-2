package rs.tiacgroup.fleetopsprovider.model.dto;

public record PremiumVehicleDto(
        String make,
        String model,
        Integer modelYear,
        String fuelType,
        String engine,
        String vehicleStatus
) {
}
