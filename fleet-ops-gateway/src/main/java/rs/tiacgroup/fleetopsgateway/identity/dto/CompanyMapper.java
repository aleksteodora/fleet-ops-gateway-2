package rs.tiacgroup.fleetopsgateway.identity.dto;

import org.mapstruct.Mapper;
import rs.tiacgroup.fleetopsgateway.identity.dto.response.CompanyResponse;
import rs.tiacgroup.fleetopsgateway.identity.entity.Company;

@Mapper(componentModel = "spring")
public interface CompanyMapper {

    CompanyResponse toResponse(Company company);
}