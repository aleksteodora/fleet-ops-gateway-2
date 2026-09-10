package rs.tiacgroup.fleetopsgateway.identity.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import rs.tiacgroup.fleetopsgateway.identity.dto.CompanyMapper;
import rs.tiacgroup.fleetopsgateway.identity.dto.response.CompanyResponse;
import rs.tiacgroup.fleetopsgateway.identity.entity.Company;
import rs.tiacgroup.fleetopsgateway.identity.repository.CompanyRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private CompanyMapper companyMapper;

    private CompanyService companyService;

    @BeforeEach
    void setUp() {
        companyService = new CompanyService(companyRepository, companyMapper);
    }

    @Test
    void listCompanies_shouldReturnMappedPage() {
        // given
        Company company = new Company("Test Company");
        CompanyResponse response = new CompanyResponse(
                1L, "Test Company", true, LocalDateTime.now(), LocalDateTime.now());
        Pageable pageable = PageRequest.of(0, 10);
        Page<Company> companyPage = new PageImpl<>(List.of(company), pageable, 1);

        when(companyRepository.findAll(pageable)).thenReturn(companyPage);
        when(companyMapper.toResponse(company)).thenReturn(response);

        // when
        Page<CompanyResponse> result = companyService.listCompanies(pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst()).isEqualTo(response);
        verify(companyRepository).findAll(pageable);
        verify(companyMapper).toResponse(company);
    }
}