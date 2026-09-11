package rs.tiacgroup.fleetopsgateway.identity.dto;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import rs.tiacgroup.fleetopsgateway.identity.dto.request.CreateCompanyRequest;
import rs.tiacgroup.fleetopsgateway.identity.dto.request.UpdateCompanyRequest;
import rs.tiacgroup.fleetopsgateway.identity.dto.response.CompanyResponse;
import rs.tiacgroup.fleetopsgateway.identity.entity.Company;

@Mapper(componentModel = "spring")
public interface CompanyMapper {

    CompanyResponse toResponse(Company company);

    Company toEntity(CreateCompanyRequest request);

    void updateEntityFromRequest(UpdateCompanyRequest request, @MappingTarget Company company);
}