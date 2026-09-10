package rs.tiacgroup.fleetopsgateway.identity.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import rs.tiacgroup.fleetopsgateway.identity.dto.CompanyMapper;
import rs.tiacgroup.fleetopsgateway.identity.dto.request.CreateCompanyRequest;
import rs.tiacgroup.fleetopsgateway.identity.dto.response.CompanyResponse;
import rs.tiacgroup.fleetopsgateway.identity.entity.Company;
import rs.tiacgroup.fleetopsgateway.identity.exception.CompanyAlreadyExistsException;
import rs.tiacgroup.fleetopsgateway.identity.repository.CompanyRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private CompanyMapper companyMapper;

    @InjectMocks
    private CompanyService companyService;

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

    @Test
    void createCompany_shouldSaveAndReturnMappedResponse() {
        // given
        CreateCompanyRequest request = new CreateCompanyRequest("New Company");
        Company mappedCompany = new Company("New Company");
        Company savedCompany = new Company("New Company");
        CompanyResponse expectedResponse = new CompanyResponse(
                1L, "New Company", true, LocalDateTime.now(), LocalDateTime.now());

        when(companyMapper.toEntity(request)).thenReturn(mappedCompany);
        when(companyRepository.saveAndFlush(mappedCompany)).thenReturn(savedCompany);
        when(companyMapper.toResponse(savedCompany)).thenReturn(expectedResponse);

        // when
        CompanyResponse result = companyService.createCompany(request);

        // then
        assertThat(result).isEqualTo(expectedResponse);
        verify(companyRepository).saveAndFlush(mappedCompany);
    }

    @Test
    void createCompany_shouldSetActiveTrueBeforeSaving() {
        // given
        CreateCompanyRequest request = new CreateCompanyRequest("New Company");
        Company mappedCompany = new Company("New Company");
        Company savedCompany = new Company("New Company");

        when(companyMapper.toEntity(request)).thenReturn(mappedCompany);
        when(companyRepository.saveAndFlush(any(Company.class))).thenReturn(savedCompany);
        when(companyMapper.toResponse(savedCompany)).thenReturn(
                new CompanyResponse(1L, "New Company", true, LocalDateTime.now(), LocalDateTime.now()));

        ArgumentCaptor<Company> companyCaptor = ArgumentCaptor.forClass(Company.class);

        // when
        companyService.createCompany(request);

        // then
        verify(companyRepository).saveAndFlush(companyCaptor.capture());
        assertThat(companyCaptor.getValue().isActive()).isTrue();
    }

    @Test
    void createCompany_shouldThrowExceptionWhenNameAlreadyExists() {
        // given
        CreateCompanyRequest request = new CreateCompanyRequest("Existing Company");
        Company mappedCompany = new Company("Existing Company");

        when(companyMapper.toEntity(request)).thenReturn(mappedCompany);
        when(companyRepository.saveAndFlush(mappedCompany))
                .thenThrow(new DataIntegrityViolationException("duplicate key"));

        // when / then
        assertThatThrownBy(() -> companyService.createCompany(request))
                .isInstanceOf(CompanyAlreadyExistsException.class)
                .hasMessageContaining("Existing Company");
    }
}