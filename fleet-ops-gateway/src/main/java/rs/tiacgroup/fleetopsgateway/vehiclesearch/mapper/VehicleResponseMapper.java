package rs.tiacgroup.fleetopsgateway.vehiclesearch.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ValueMapping;
import rs.tiacgroup.fleetopsgateway.searchhistory.entity.FuelType;
import rs.tiacgroup.fleetopsgateway.searchhistory.entity.VehicleStatus;
import rs.tiacgroup.fleetopsgateway.vehiclesearch.integration.model.FreeProviderResponse;
import rs.tiacgroup.fleetopsgateway.vehiclesearch.integration.model.PremiumProviderResponse;
import rs.tiacgroup.fleetopsgateway.vehiclesearch.integration.model.VehicleData;

@Mapper(componentModel = "spring")
public interface VehicleResponseMapper {

    @Mapping(target = "make", source = "manufacturerName")
    @Mapping(target = "model", source = "vehicleModel")
    @Mapping(target = "engine", source = "engineDescription")
    VehicleData toVehicleData(FreeProviderResponse response);

    VehicleData toVehicleData(PremiumProviderResponse response);

    @ValueMapping(target = "NO_VALUE", source = MappingConstants.ANY_REMAINING)
    FuelType mapFuelType(String value);

    @ValueMapping(target = "NO_VALUE", source = MappingConstants.ANY_REMAINING)
    VehicleStatus mapVehicleStatus(String value);
}