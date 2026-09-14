package rs.tiacgroup.fleetopsgateway.vehiclesearch.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import rs.tiacgroup.fleetopsgateway.vehiclesearch.integration.model.FreeProviderResponse;
import rs.tiacgroup.fleetopsgateway.vehiclesearch.integration.model.PremiumProviderResponse;
import rs.tiacgroup.fleetopsgateway.vehiclesearch.integration.model.VehicleData;

import static org.assertj.core.api.Assertions.assertThat;

class VehicleResponseMapperTest {

    private static final String MAKE = "BMW";
    private static final String MODEL = "320d";
    private static final Integer MODEL_YEAR = 2020;
    private static final String FUEL_TYPE = "DIESEL";
    private static final String ENGINE = "2.0";
    private static final String VEHICLE_STATUS = "ACTIVE";

    private VehicleResponseMapper vehicleResponseMapper;

    @BeforeEach
    void setUp() {
        vehicleResponseMapper = Mappers.getMapper(VehicleResponseMapper.class);
    }

    @Test
    void toVehicleData_shouldMapFreeProviderResponseFieldsCorrectly() {
        // given
        FreeProviderResponse response = new FreeProviderResponse(
                MAKE, MODEL, MODEL_YEAR, FUEL_TYPE, ENGINE, VEHICLE_STATUS);

        VehicleData expected = new VehicleData(
                MAKE, MODEL, MODEL_YEAR, FUEL_TYPE, ENGINE, VEHICLE_STATUS);

        // when
        VehicleData actual = vehicleResponseMapper.toVehicleData(response);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void toVehicleData_shouldMapPremiumProviderResponseFieldsCorrectly() {
        // given
        PremiumProviderResponse response = new PremiumProviderResponse(
                MAKE, MODEL, MODEL_YEAR, FUEL_TYPE, ENGINE, VEHICLE_STATUS);

        VehicleData expected = new VehicleData(
                MAKE, MODEL, MODEL_YEAR, FUEL_TYPE, ENGINE, VEHICLE_STATUS);

        // when
        VehicleData actual = vehicleResponseMapper.toVehicleData(response);

        // then
        assertThat(actual).isEqualTo(expected);
    }
}