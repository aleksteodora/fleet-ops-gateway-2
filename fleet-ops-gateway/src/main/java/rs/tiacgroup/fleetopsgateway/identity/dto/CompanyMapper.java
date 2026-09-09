package rs.tiacgroup.fleetopsgateway.identity.dto;

import rs.tiacgroup.fleetopsgateway.identity.entity.Company;
import rs.tiacgroup.fleetopsgateway.identity.dto.response.CompanyResponse;

public class CompanyMapper {

    private CompanyMapper() {
    }

    public static CompanyResponse toResponse(Company company) {
        return new CompanyResponse(
                company.getId(),
                company.getName(),
                company.isActive(),
                company.getCreatedAt(),
                company.getUpdatedAt()
        );
    }
}