package rs.tiacgroup.fleetopsprovider.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import rs.tiacgroup.fleetopsprovider.model.Vehicle;
import rs.tiacgroup.fleetopsprovider.model.dto.FreeVehicleDto;
import rs.tiacgroup.fleetopsprovider.model.dto.PremiumVehicleDto;
import rs.tiacgroup.fleetopsprovider.model.enums.FuelType;
import rs.tiacgroup.fleetopsprovider.model.enums.VehicleStatus;

import static org.assertj.core.api.Assertions.assertThat;

class VehicleToVehicleDtoMapperTest {

    private static final String VIN = "1HGCM82633A123456";
    private static final String MAKE = "Toyota";
    private static final String MODEL = "Corolla";
    private static final int MODEL_YEAR = 2024;
    private static final String ENGINE = "2.0L";

    private final VehicleToVehicleDtoMapper mapper = Mappers.getMapper(VehicleToVehicleDtoMapper.class);

    @Test
    void shouldMapVehicleToFreeVehicleDto() {
        Vehicle vehicle = createVehicle();

        FreeVehicleDto result = mapper.mapToFreeVehicleDto(vehicle);

        FreeVehicleDto expected = new FreeVehicleDto(
                MAKE,
                MODEL,
                MODEL_YEAR,
                FuelType.DIESEL.name(),
                ENGINE,
                VehicleStatus.ACTIVE.name()
        );

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void shouldMapVehicleToPremiumVehicleDto() {
        Vehicle vehicle = createVehicle();

        PremiumVehicleDto result = mapper.mapToPremiumVehicleDto(vehicle);

        PremiumVehicleDto expected = new PremiumVehicleDto(
                MAKE,
                MODEL,
                MODEL_YEAR,
                FuelType.DIESEL.name(),
                ENGINE,
                VehicleStatus.ACTIVE.name()
        );

        assertThat(result).isEqualTo(expected);
    }

    private Vehicle createVehicle() {
        return new Vehicle(
                VIN,
                MAKE,
                MODEL,
                MODEL_YEAR,
                FuelType.DIESEL,
                ENGINE,
                VehicleStatus.ACTIVE
        );
    }
}
