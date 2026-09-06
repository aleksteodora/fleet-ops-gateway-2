package rs.tiacgroup.fleetopsprovider.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rs.tiacgroup.fleetopsprovider.mapper.VehicleToVehicleDtoMapper;
import rs.tiacgroup.fleetopsprovider.model.Vehicle;
import rs.tiacgroup.fleetopsprovider.model.dto.FreeVehicleDto;
import rs.tiacgroup.fleetopsprovider.model.enums.FuelType;
import rs.tiacgroup.fleetopsprovider.model.enums.VehicleStatus;
import rs.tiacgroup.fleetopsprovider.repository.VehicleRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FreeThirdPartyServiceTest {

    private static final String VIN_1 = "1HGCM82633A123456";
    private static final String VIN_2 = "WVWZZZ1JZXW000001";

    private static final String MAKE = "Toyota";
    private static final String MODEL = "Corolla";
    private static final int MODEL_YEAR = 2024;
    private static final String ENGINE = "2.0L";

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private VehicleToVehicleDtoMapper vehicleToVehicleDtoMapper;

    @InjectMocks
    private FreeThirdPartyService freeThirdPartyService;

    @Test
    void shouldReturnEmptyListWhenQueryIsNull() {
        List<FreeVehicleDto> result = freeThirdPartyService.search(null);

        assertThat(result).isEmpty();
        verifyNoInteractions(vehicleRepository, vehicleToVehicleDtoMapper);
    }

    @Test
    void shouldReturnEmptyListWhenQueryIsEmpty() {
        List<FreeVehicleDto> result = freeThirdPartyService.search("");

        assertThat(result).isEmpty();
        verifyNoInteractions(vehicleRepository, vehicleToVehicleDtoMapper);
    }

    @Test
    void shouldReturnEmptyListWhenQueryContainsOnlyWhitespace() {
        List<FreeVehicleDto> result = freeThirdPartyService.search("   ");

        assertThat(result).isEmpty();
        verifyNoInteractions(vehicleRepository, vehicleToVehicleDtoMapper);
    }

    @Test
    void shouldReturnMappedVehicleWhenVinMatches() {
        Vehicle vehicle = createVehicle(VIN_1);
        FreeVehicleDto expectedDto = createDto();
        when(vehicleRepository.findAll()).thenReturn(List.of(vehicle));
        when(vehicleToVehicleDtoMapper.mapToFreeVehicleDto(vehicle)).thenReturn(expectedDto);

        List<FreeVehicleDto> result = freeThirdPartyService.search(VIN_1);

        assertThat(result).containsExactly(expectedDto);
        verify(vehicleToVehicleDtoMapper).mapToFreeVehicleDto(vehicle);
    }

    @Test
    void shouldMatchVinCaseInsensitively() {
        Vehicle vehicle = createVehicle(VIN_1);
        FreeVehicleDto expectedDto = createDto();

        when(vehicleRepository.findAll()).thenReturn(List.of(vehicle));
        when(vehicleToVehicleDtoMapper.mapToFreeVehicleDto(vehicle)).thenReturn(expectedDto);

        List<FreeVehicleDto> result = freeThirdPartyService.search(VIN_1.toLowerCase());

        assertThat(result).containsExactly(expectedDto);
        verify(vehicleToVehicleDtoMapper).mapToFreeVehicleDto(vehicle);
    }

    @Test
    void shouldReturnEmptyListWhenVinDoesNotMatch() {
        Vehicle vehicle = createVehicle(VIN_1);
        when(vehicleRepository.findAll()).thenReturn(List.of(vehicle));

        List<FreeVehicleDto> result = freeThirdPartyService.search(VIN_2);

        assertThat(result).isEmpty();
        verifyNoInteractions(vehicleToVehicleDtoMapper);
    }

    @Test
    void shouldIgnoreVehicleWithNullVin() {
        Vehicle vehicleWithNullVin = createVehicle(null);
        when(vehicleRepository.findAll()).thenReturn(List.of(vehicleWithNullVin));

        List<FreeVehicleDto> result = freeThirdPartyService.search(VIN_1);

        assertThat(result).isEmpty();
        verifyNoInteractions(vehicleToVehicleDtoMapper);
    }

    @Test
    void shouldReturnOnlyMatchingVehicles() {
        Vehicle matchingVehicle = createVehicle(VIN_1);
        Vehicle nonMatchingVehicle = createVehicle(VIN_2);
        FreeVehicleDto expectedDto = createDto();
        when(vehicleRepository.findAll()).thenReturn(List.of(matchingVehicle, nonMatchingVehicle));
        when(vehicleToVehicleDtoMapper.mapToFreeVehicleDto(matchingVehicle)).thenReturn(expectedDto);

        List<FreeVehicleDto> result = freeThirdPartyService.search(VIN_1);

        assertThat(result).containsExactly(expectedDto);
        verify(vehicleToVehicleDtoMapper).mapToFreeVehicleDto(matchingVehicle);
        verifyNoMoreInteractions(vehicleToVehicleDtoMapper);
    }

    @Test
    void shouldReturnAllMatchingVehicles() {
        Vehicle firstVehicle = createVehicle(VIN_1);
        Vehicle secondVehicle = createVehicle(VIN_1);
        FreeVehicleDto firstDto = createDto();
        FreeVehicleDto secondDto = createDto();
        when(vehicleRepository.findAll()).thenReturn(List.of(firstVehicle, secondVehicle));
        when(vehicleToVehicleDtoMapper.mapToFreeVehicleDto(firstVehicle)).thenReturn(firstDto);
        when(vehicleToVehicleDtoMapper.mapToFreeVehicleDto(secondVehicle)).thenReturn(secondDto);

        List<FreeVehicleDto> result = freeThirdPartyService.search(VIN_1);

        assertThat(result).containsExactly(firstDto, secondDto);
        verify(vehicleToVehicleDtoMapper).mapToFreeVehicleDto(firstVehicle);
        verify(vehicleToVehicleDtoMapper).mapToFreeVehicleDto(secondVehicle);
    }

    private Vehicle createVehicle(String vin) {
        return new Vehicle(
                vin,
                MAKE,
                MODEL,
                MODEL_YEAR,
                FuelType.DIESEL,
                ENGINE,
                VehicleStatus.ACTIVE
        );
    }

    private FreeVehicleDto createDto() {
        return new FreeVehicleDto(
                MAKE,
                MODEL,
                MODEL_YEAR,
                FuelType.DIESEL.name(),
                ENGINE,
                VehicleStatus.ACTIVE.name()
        );
    }
}
