package rs.tiacgroup.fleetopsprovider.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import rs.tiacgroup.fleetopsprovider.mapper.VehicleToVehicleDtoMapper;
import rs.tiacgroup.fleetopsprovider.model.Vehicle;
import rs.tiacgroup.fleetopsprovider.model.dto.FreeVehicleDto;
import rs.tiacgroup.fleetopsprovider.repository.VehicleRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FreeThirdPartyService {

    private final VehicleRepository vehicleRepository;
    private final VehicleToVehicleDtoMapper vehicleToVehicleDtoMapper;

    public List<FreeVehicleDto> search(String query) {
        if (StringUtils.isBlank(query)) {
            return List.of();
        }

        String normalizedQuery = query.toLowerCase();

        return vehicleRepository.findAll().stream()
                .filter(vehicle -> matches(vehicle, normalizedQuery))
                .map(vehicleToVehicleDtoMapper::mapToFreeVehicleDto)
                .toList();
    }

    private boolean matches(Vehicle vehicle, String query) {
        String vin = vehicle.getVin();
        return vin != null && vin.toLowerCase().equalsIgnoreCase(query);
    }
}
