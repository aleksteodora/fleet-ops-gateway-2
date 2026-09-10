package rs.tiacgroup.fleetopsgateway.identity.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import rs.tiacgroup.fleetopsgateway.identity.dto.response.CompanyResponse;
import rs.tiacgroup.fleetopsgateway.identity.entity.Company;

import static org.assertj.core.api.Assertions.assertThat;

class CompanyMapperTest {

    private CompanyMapper companyMapper;

    @BeforeEach
    void setUp() {
        companyMapper = Mappers.getMapper(CompanyMapper.class);
    }

    @Test
    void toResponse_shouldMapAllFieldsCorrectly() {
        // given
        Company company = new Company("Test Company");
        CompanyResponse expected = new CompanyResponse(
                company.getId(),
                company.getName(),
                company.isActive(),
                company.getCreatedAt(),
                company.getUpdatedAt()
        );

        // when
        CompanyResponse actual = companyMapper.toResponse(company);

        // then
        assertThat(actual).isEqualTo(expected);
    }
}