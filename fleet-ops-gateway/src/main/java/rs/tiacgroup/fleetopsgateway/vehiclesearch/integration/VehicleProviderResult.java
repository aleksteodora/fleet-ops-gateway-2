package rs.tiacgroup.fleetopsgateway.vehiclesearch.integration;

import rs.tiacgroup.fleetopsgateway.vehiclesearch.integration.model.VehicleData;

public record VehicleProviderResult(
        Outcome outcome,
        VehicleData vehicleData
) {
    public enum Outcome {
        FOUND,
        NOT_FOUND,
        UNAVAILABLE
    }
}