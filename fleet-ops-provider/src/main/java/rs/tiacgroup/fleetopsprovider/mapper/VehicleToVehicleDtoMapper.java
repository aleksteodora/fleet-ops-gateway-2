package rs.tiacgroup.fleetopsprovider.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import rs.tiacgroup.fleetopsprovider.model.Vehicle;
import rs.tiacgroup.fleetopsprovider.model.dto.FreeVehicleDto;
import rs.tiacgroup.fleetopsprovider.model.dto.PremiumVehicleDto;

@Mapper(componentModel = "spring")
public interface VehicleToVehicleDtoMapper {

    @Mapping(source = "make", target = "manufacturerName")
    @Mapping(source = "model", target = "vehicleModel")
    @Mapping(source = "engine", target = "engineDescription")
    FreeVehicleDto mapToFreeVehicleDto(Vehicle vehicle);

    PremiumVehicleDto mapToPremiumVehicleDto(Vehicle vehicle);
}
