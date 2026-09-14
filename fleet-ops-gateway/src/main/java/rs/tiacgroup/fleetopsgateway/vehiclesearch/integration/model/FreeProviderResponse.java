package rs.tiacgroup.fleetopsgateway.vehiclesearch.integration.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FreeProviderResponse(

        @JsonProperty("manufacturer_name")
        String manufacturerName,

        @JsonProperty("vehicle_model")
        String vehicleModel,

        @JsonProperty("model_year")
        Integer modelYear,

        @JsonProperty("fuel_type")
        String fuelType,

        @JsonProperty("engine_description")
        String engineDescription,

        @JsonProperty("vehicle_status")
        String vehicleStatus

) {
}