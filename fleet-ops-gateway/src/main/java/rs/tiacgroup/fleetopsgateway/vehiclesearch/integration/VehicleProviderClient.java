package rs.tiacgroup.fleetopsgateway.vehiclesearch.integration;

public interface VehicleProviderClient {
    VehicleProviderResult search(String vin);
}